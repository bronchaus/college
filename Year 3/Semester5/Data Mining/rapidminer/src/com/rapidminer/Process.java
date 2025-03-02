/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2013 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.event.EventListenerList;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.rapidminer.datatable.DataTable;
import com.rapidminer.datatable.SimpleDataTable;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.io.process.XMLImporter;
import com.rapidminer.operator.Annotations;
import com.rapidminer.operator.DebugMode;
import com.rapidminer.operator.DummyOperator;
import com.rapidminer.operator.ExecutionMode;
import com.rapidminer.operator.ExecutionUnit;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ProcessRootOperator;
import com.rapidminer.operator.ProcessStoppedException;
import com.rapidminer.operator.UnknownParameterInformation;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.nio.file.RepositoryBlobObject;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.Port;
import com.rapidminer.report.ReportStream;
import com.rapidminer.repository.BlobEntry;
import com.rapidminer.repository.Entry;
import com.rapidminer.repository.IOObjectEntry;
import com.rapidminer.repository.MalformedRepositoryLocationException;
import com.rapidminer.repository.RepositoryAccessor;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.repository.RepositoryManager;
import com.rapidminer.tools.AbstractObservable;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.LoggingHandler;
import com.rapidminer.tools.Observable;
import com.rapidminer.tools.Observer;
import com.rapidminer.tools.OperatorService;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.ProgressListener;
import com.rapidminer.tools.RandomGenerator;
import com.rapidminer.tools.ResultService;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.WebServiceTools;
import com.rapidminer.tools.WrapperLoggingHandler;
import com.rapidminer.tools.XMLException;
import com.rapidminer.tools.container.Pair;
import com.rapidminer.tools.usagestats.OperatorStatisticsValue;
import com.rapidminer.tools.usagestats.UsageStatistics;

/**
 * <p>
 * This class was introduced to avoid confusing handling of operator maps and other stuff when a new process definition
 * is created. It is also necessary for file name resolving and breakpoint handling.
 * </p>
 * 
 * <p>
 * If you want to use RapidMiner from your own application the best way is often to create a process definition from
 * scratch (by adding the complete operator tree to the process' root operator) or from a file (for example created with
 * the GUI beforehand) and start it by invoking the {@link #run()} method.
 * </p>
 * 
 * <p>
 * Observers can listen to changes of the associated file, repository location, and context.
 * </p>
 * TODO: Add reasonable class comment
 * 
 * @author Ingo Mierswa
 */
public class Process extends AbstractObservable<Process> implements Cloneable {

	public static final int PROCESS_STATE_UNKNOWN = -1;
	public static final int PROCESS_STATE_STOPPED = 0;
	public static final int PROCESS_STATE_PAUSED = 1;
	public static final int PROCESS_STATE_RUNNING = 2;

	/** The root operator of the process. */
	private ProcessRootOperator rootOperator = null;

	/** This is the operator which is currently applied. */
	private Operator currentOperator;

	/**
	 * The process might be connected to this file or repository location which is then used to resolve relative file
	 * names which might be defined as parameters.
	 */
	private ProcessLocation processLocation;

	/** Indicates if the original process file has been changed by import rules.
	 * If this happens, overwriting will destroy the backward compatibility. This flag indicates that
	 * this would happen during saving. */
	private boolean isProcessConverted = false;

	/** This list contains all unknown parameter information which existed during the loading of the process. */
	private final List<UnknownParameterInformation> unknownParameterInformation = new LinkedList<UnknownParameterInformation>();

	/** The listeners for breakpoints. */
	private final List<BreakpointListener> breakpointListeners = new LinkedList<BreakpointListener>();

	/** The listeners for logging (data tables). */
	private final List<LoggingListener> loggingListeners = new LinkedList<LoggingListener>();
	
	/**THe listeners for the Process stroage. */
	private final List<ProcessStorageListener> storageListeners = new LinkedList<ProcessStorageListener>();

	/** The macro handler can be used to replace (user defined) macro strings. */
	private final MacroHandler macroHandler = new MacroHandler(this);

	/**
	 * This map holds the names of all operators in the process. Operators are automatically registered during adding
	 * and unregistered after removal.
	 */
	private Map<String, Operator> operatorNameMap = new HashMap<String, Operator>();

	/**
	 * Maps names of ProcessLog operators to Objects, that these Operators use for collecting statistics (objects of
	 * type {@link DataTable}).
	 */
	private final Map<String, DataTable> dataTableMap = new HashMap<String, DataTable>();

	/**
	 * Maps names of report streams to reportStream objects
	 */
	private final Map<String, ReportStream> reportStreamMap = new HashMap<String, ReportStream>();

	/**
	 * Stores IOObjects according to a specified name.
	 */
	private final Map<String, IOObject> storageMap = new HashMap<String, IOObject>();

	/** Indicates the current process state. */
	private int processState = PROCESS_STATE_STOPPED;

	/** Indicates whether operators should be executed always or only when dirty. */
	private transient ExecutionMode executionMode = ExecutionMode.ALWAYS;

	/** Indicates whether we are updating meta data. */
	private transient DebugMode debugMode = DebugMode.DEBUG_OFF;

	private transient final Logger logger = makeLogger();

	/** @deprecated Use {@link #getLogger()} */
	@Deprecated
	private transient final LoggingHandler logService = new WrapperLoggingHandler(logger);

	private ProcessContext context = new ProcessContext();

	/** Message generated during import by {@link XMLImporter}. */
	private String importMessage;

	private final Annotations annotations = new Annotations();

	private RepositoryAccessor repositoryAccessor;


	// -------------------
	// Constructors
	// -------------------

	/** Constructs an process consisting only of a SimpleOperatorChain. */
	public Process() {
		try {
			ProcessRootOperator root = OperatorService.createOperator(ProcessRootOperator.class);
			root.rename(root.getOperatorDescription().getName());
			setRootOperator(root);
		} catch (Exception e) {
			throw new RuntimeException("Cannot initialize root operator of the process: " + e.getMessage(), e);
		}
		initContext();
	}

	public Process(File file) throws IOException, XMLException {
		this(file, null);
	}

	/**
	 * Creates a new process from the given process file. This might have been created with the GUI beforehand.
	 */
	public Process(File file, ProgressListener progressListener) throws IOException, XMLException {
		this.processLocation = new FileProcessLocation(file);
		initContext();
		Reader in = null;
		try {
			in = new InputStreamReader(new FileInputStream(file), "UTF-8");
			readProcess(in, progressListener);
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null)
				in.close();
		}
	}

	/**
	 * Creates a new process from the given XML copying state information not covered by the XML from the parameter
	 * process.
	 */
	public Process(String xml, Process process) throws IOException, XMLException {
		this(xml);
		this.processLocation = process.processLocation;
	}

	/** Reads an process configuration from an XML String. */
	public Process(String xmlString) throws IOException, XMLException {
		initContext();
		StringReader in = new StringReader(xmlString);
		readProcess(in);
		in.close();
	}

	/** Reads an process configuration from the given reader. */
	public Process(Reader in) throws IOException, XMLException {
		initContext();
		readProcess(in);
	}

	/** Reads an process configuration from the given stream. */
	public Process(InputStream in) throws IOException, XMLException {
		initContext();
		readProcess(new InputStreamReader(in, XMLImporter.PROCESS_FILE_CHARSET));
	}

	/** Reads an process configuration from the given URL. */
	public Process(URL url) throws IOException, XMLException {
		initContext();
		Reader in = new InputStreamReader(WebServiceTools.openStreamFromURL(url), getEncoding(null));
		readProcess(in);
		in.close();
	}

	protected Logger makeLogger() {
		return Logger.getLogger(Process.class.getName());
	}

	private void initContext() {
		getContext().addObserver(delegatingContextObserver, false);
	}

	/**
	 * Clone constructor. Makes a deep clone of the operator tree and the process file. The same applies for the
	 * operatorNameMap. The breakpoint listeners are copied by reference and all other fields are initialized like for a
	 * fresh process.
	 */
	private Process(Process other) {
		this();
		setRootOperator((ProcessRootOperator) other.rootOperator.cloneOperator(other.rootOperator.getName(), false));
		this.currentOperator = null;
		if (other.processLocation != null)
			this.processLocation = other.processLocation;
		else
			this.processLocation = null;
	}

	private void initLogging(int logVerbosity) {
		if (logVerbosity >= 0) {
			logger.setLevel(WrapperLoggingHandler.LEVELS[logVerbosity]);
		} else {
			logger.setLevel(Level.INFO);
		}
	}

	@Override
	public Object clone() {
		return new Process(this);
	}

	/**
	 * @deprecated Use {@link #setProcessState(int)} instead
	 */
	@Deprecated
	public synchronized void setExperimentState(int state) {
		setProcessState(state);
	}

	private void setProcessState(int state) {
		this.processState = state;
	}

	/**
	 * @deprecated Use {@link #getProcessState()} instead
	 */
	@Deprecated
	public synchronized int getExperimentState() {
		return getProcessState();
	}

	public int getProcessState() {
		return this.processState;
	}

	// -------------------------
	// Logging
	// -------------------------

	public LoggingHandler getLog() {
		return this.logService;
	}

	public Logger getLogger() {
		return this.logger;
	}

	// -------------------------
	// Macro Handler
	// -------------------------

	/** Returns the macro handler. */
	public MacroHandler getMacroHandler() {
		return this.macroHandler;
	}

	/** Clears all macros. */
	public void clearMacros() {
		this.getMacroHandler().clear();
	}

	// -------------------------
	// IOObject Storage
	// -------------------------

	/** Returns the macro handler. */
	public void store(String name, IOObject object) {
		this.storageMap.put(name, object);
	}

	/** Retrieves the stored object. */
	public IOObject retrieve(String name, boolean remove) {
		if (remove) {
			return this.storageMap.remove(name);
		} else {
			return this.storageMap.get(name);
		}
	}

	/** Clears all macros. */
	public void clearStorage() {
		this.storageMap.clear();
	}

	// -------------------------
	// Data Tables (Logging)
	// -------------------------

	/** Adds the given logging listener. */
	public void addLoggingListener(LoggingListener loggingListener) {
		this.loggingListeners.add(loggingListener);
	}

	/** Removes the given logging listener. */
	public void removeLoggingListener(LoggingListener loggingListener) {
		this.loggingListeners.remove(loggingListener);
	}

	/** Returns true if a data table object with the given name exists. */
	public boolean dataTableExists(String name) {
		return dataTableMap.get(name) != null;
	}

	/**
	 * Adds the given data table.
	 */
	public void addDataTable(DataTable table) {
		dataTableMap.put(table.getName(), table);
		for (LoggingListener listener : loggingListeners) {
			listener.addDataTable(table);
		}
	}

	/** Clears a single data table, i.e. removes all entries. */
	public void clearDataTable(String name) {
		DataTable table = getDataTable(name);
		if (table != null) {
			if (table instanceof SimpleDataTable) {
				((SimpleDataTable) table).clear();
			}
		}
	}

	/** Deletes a single data table. */
	public void deleteDataTable(String name) {
		if (dataTableExists(name)) {
			DataTable table = dataTableMap.remove(name);

			for (LoggingListener listener : loggingListeners) {
				listener.removeDataTable(table);
			}
		}
	}

	/**
	 * Returns the data table associated with the given name. If the name was not used yet, an empty DataTable object is
	 * created with the given columnNames.
	 */
	public DataTable getDataTable(String name) {
		return dataTableMap.get(name);
	}

	/** Returns all data tables. */
	public Collection<DataTable> getDataTables() {
		return dataTableMap.values();
	}

	/** Removes all data tables before running a new process. */
	private void clearDataTables() {
		dataTableMap.clear();
	}

	// ------------------------------
	// Report Streams
	// ------------------------------

	/**
	 * This method adds a new report stream with the given name
	 */
	public void addReportStream(ReportStream stream) {
		reportStreamMap.put(stream.getName(), stream);
	}

	/**
	 * Returns the reportStream with given name
	 */
	public ReportStream getReportStream(String name) {
		if (name == null || name.length() == 0) {
			if (reportStreamMap.size() == 1) {
				return reportStreamMap.values().iterator().next();
			} else {
				return null;
			}
		} else {
			return reportStreamMap.get(name);
		}
	}

	/**
	 * Removes this reportStream from process. This report Stream will not be notified about new report items.
	 * 
	 * @param name
	 *            of the report stream given in the ReportGenerator operator
	 */
	public void removeReportStream(String name) {
		reportStreamMap.remove(name);
	}

	public void clearReportStreams() {
		reportStreamMap.clear();
	}

	// ----------------------
	// Operator Handling
	// ----------------------

	/** Sets the current root operator. This might lead to a new registering of operator names. */
	public void setRootOperator(ProcessRootOperator root) {
		if (this.rootOperator != null) {
			this.rootOperator.removeObserver(delegatingOperatorObserver);
		}
		this.rootOperator = root;
		this.rootOperator.addObserver(delegatingOperatorObserver, false);
		this.operatorNameMap.clear();
		this.rootOperator.setProcess(this);
	}

	/** Delivers the current root operator. */
	public ProcessRootOperator getRootOperator() {
		return rootOperator;
	}

	/** Returns the operator with the given name. */
	public Operator getOperator(String name) {
		return operatorNameMap.get(name);
	}

	/** Returns the operator that is currently being executed. */
	public Operator getCurrentOperator() {
		return currentOperator;
	}

	/** Returns a Collection view of all operators. */
	public Collection<Operator> getAllOperators() {
		List<Operator> result = rootOperator.getAllInnerOperators();
		result.add(0, rootOperator);
		return result;
	}

	/** Returns a Set view of all operator names (i.e. Strings). */
	public Collection<String> getAllOperatorNames() {
		Collection<String> allNames = new LinkedList<String>();
		for (Operator o : getAllOperators()) {
			allNames.add(o.getName());
		}
		return allNames;
	}

	/** Sets the operator that is currently being executed. */
	public void setCurrentOperator(Operator operator) {
		this.currentOperator = operator;
	}

	// -------------------------------------
	// start, stop, resume, breakpoints
	// -------------------------------------

	/** We synchronize on this object to wait and resume operation. */
	private final Object breakpointLock = new Object();

	/** Pauses the process at a breakpoint. */
	public void pause(Operator operator, IOContainer iocontainer, int breakpointType) {
		setProcessState(PROCESS_STATE_PAUSED);
		fireBreakpointEvent(operator, iocontainer, breakpointType);
		while (getProcessState() == Process.PROCESS_STATE_PAUSED) {
			synchronized (breakpointLock) {
				try {
					breakpointLock.wait();
				} catch (InterruptedException e) {}
			}
		}
	}

	/** Resumes the process after it has been paused. */
	public void resume() {
		setProcessState(PROCESS_STATE_RUNNING);
		synchronized (breakpointLock) {
			breakpointLock.notifyAll();
		}
		fireResumeEvent();
	}

	/** Stops the process as soon as possible. */
	public void stop() {
		this.setProcessState(PROCESS_STATE_STOPPED);
		synchronized (breakpointLock) {
			breakpointLock.notifyAll();
		}
	}

	/** Stops the process as soon as possible. */
	public void pause() {
		this.setProcessState(PROCESS_STATE_PAUSED);
	}

	/** Returns true iff the process should be stopped. */
	public boolean shouldStop() {
		return getProcessState() == PROCESS_STATE_STOPPED;
	}

	/** Returns true iff the process should be stopped. */
	public boolean shouldPause() {
		return getProcessState() == PROCESS_STATE_PAUSED;
	}

	// --------------------
	// ProcessStorageListener
	// --------------------
	
	/** Adds a ProcessStorageListener.  */
	
	public void addProcessStorageListener(ProcessStorageListener listener){
		storageListeners.add(listener);
	}
	
	public void removeProcessStorageListener(ProcessStorageListener listener){
		storageListeners.remove(listener);
	}
	
	private void fireProcessStoredEvent(Process process){
		LinkedList<ProcessStorageListener> listeners = new LinkedList<ProcessStorageListener>(storageListeners);
		for (ProcessStorageListener l : listeners){
			l.stored(process);
		}
	}
	
	// --------------------
	// Breakpoint Handling
	// --------------------
	
	/** Adds a breakpoint listener. */
	public void addBreakpointListener(BreakpointListener listener) {
		breakpointListeners.add(listener);
	}
	
	/** Removes a breakpoint listener. */
	public void removeBreakpointListener(BreakpointListener listener) {
		breakpointListeners.remove(listener);
	}

	/** Fires the event that the process was paused. */
	private void fireBreakpointEvent(Operator operator, IOContainer ioContainer, int location) {
		for (BreakpointListener l : breakpointListeners) {
			l.breakpointReached(this, operator, ioContainer, location);
		}
	}

	/** Fires the event that the process was resumed. */
	public void fireResumeEvent() {
		LinkedList<BreakpointListener> l = new LinkedList<BreakpointListener>(breakpointListeners);
		for (BreakpointListener listener : l) {
			listener.resume();
		}
	}

	// -----------------
	// Checks
	// -----------------

	/**
	 * Delivers the information about unknown parameter types which occurred during process creation (from streams or
	 * files).
	 */
	public List<UnknownParameterInformation> getUnknownParameters() {
		return this.unknownParameterInformation;
	}

	/**
	 * Clears the information about unknown parameter types which occurred during process creation (from streams or
	 * files).
	 */
	public void clearUnknownParameters() {
		this.unknownParameterInformation.clear();
	}

	/**
	 * Checks for correct number of inner operators, properties, and io.
	 * 
	 * @deprecated Use {@link #checkProcess(IOContainer)} instead
	 */
	@Deprecated
	public boolean checkExperiment(IOContainer inputContainer) {
		return checkProcess(inputContainer);
	}

	/** Checks for correct number of inner operators, properties, and io. */
	public boolean checkProcess(IOContainer inputContainer) {
		rootOperator.checkAll();
		return true;
	}

	// ------------------
	// Running
	// ------------------

	/**
	 * This method initializes the process, the operators, and the services and must be invoked at the beginning of run.
	 * It also resets all apply counts.
	 */
	private final void prepareRun(int logVerbosity) throws OperatorException {
		initLogging(logVerbosity);

		setProcessState(PROCESS_STATE_RUNNING);
		getLogger().fine("Initialising process setup.");

		RandomGenerator.init(this);
		ResultService.init(this);

		// checkProcess(null);
		clearDataTables();
		clearReportStreams();
		clearMacros();
		clearStorage();
		if (getExecutionMode() != ExecutionMode.ONLY_DIRTY) {
			getRootOperator().clear(Port.CLEAR_DATA);
		}
		AttributeFactory.resetNameCounters();

		getLogger().fine("Process initialised.");
	}

	/** Loads results from the repository if specified in the {@link ProcessContext}.
	 * @param firstPort Specifies the first port which is read from the ProcessContext. This
	 * 		enables the possibility to skip ports for which input is already specified via
	 * 		the input parameter of the run() method.
	 */
	protected void loadInitialData(int firstPort) throws UserError {
		ProcessContext context = getContext();
		if (context.getInputRepositoryLocations().isEmpty()) {
			return;
		}
		getLogger().info("Loading initial data" + (firstPort > 0 ? " (starting at port " + (firstPort + 1) + ")" : "") + ".");
		for (int i = firstPort; i < context.getInputRepositoryLocations().size(); i++) {
			String location = context.getInputRepositoryLocations().get(i);
			if (location == null || location.length() == 0) {
				getLogger().fine("Input #" + (i + 1) + " not specified.");
			} else {
				if (i >= rootOperator.getSubprocess(0).getInnerSources().getNumberOfPorts()) {
					getLogger().warning("No input port available for process input #" + (i + 1) + ": " + location);
				} else {
					OutputPort port = rootOperator.getSubprocess(0).getInnerSources().getPortByIndex(i);
					RepositoryLocation loc;
					try {
						loc = resolveRepositoryLocation(location);
					} catch (MalformedRepositoryLocationException e1) {
						throw e1.makeUserError(rootOperator);
					}
					try {
						Entry entry = loc.locateEntry();
						if (entry == null) {
							throw new UserError(rootOperator, 312, loc, "Entry " + loc + " does not exist.");
						}
						if (entry instanceof IOObjectEntry) {
							getLogger().info("Assigning " + loc + " to input port " + port.getSpec() + ".");
							port.deliver(((IOObjectEntry) entry).retrieveData(null));
						} else if (entry instanceof BlobEntry) {
							getLogger().info("Assigning " + loc + " to input port " + port.getSpec() + ".");
							port.deliver(new RepositoryBlobObject(loc));
						} else {
							getLogger().info("Cannot assigning " + loc + " to input port " + port.getSpec() + ": Repository location does not reference an IOObject entry.");
							throw new UserError(rootOperator, 312, loc, "Not an IOObject entry.");
						}
					} catch (RepositoryException e) {
						throw new UserError(rootOperator, e, 312, loc, e.getMessage());
					}
				}
			}
		}
	}

	/** Stores the results in the repository if specified in the {@link ProcessContext}. */
	protected void saveResults() throws UserError {
		ProcessContext context = getContext();
		if (context.getOutputRepositoryLocations().isEmpty()) {
			return;
		}
		getLogger().info("Saving results.");
		for (int i = 0; i < context.getOutputRepositoryLocations().size(); i++) {
			String locationStr = context.getOutputRepositoryLocations().get(i);
			if (locationStr == null || locationStr.length() == 0) {
				getLogger().fine("Output #" + (i + 1) + " not specified.");
			} else {
				if (i >= rootOperator.getSubprocess(0).getInnerSinks().getNumberOfPorts()) {
					getLogger().warning("No output port corresponding to process output #" + (i + 1) + ": " + locationStr);
				} else {
					InputPort port = rootOperator.getSubprocess(0).getInnerSinks().getPortByIndex(i);
					RepositoryLocation location;
					try {
						location = rootOperator.getProcess().resolveRepositoryLocation(locationStr);
					} catch (MalformedRepositoryLocationException e1) {
						throw e1.makeUserError(rootOperator);
					}
					IOObject data = port.getDataOrNull(IOObject.class);
					if (data == null) {
						getLogger().warning("Nothing to store at " + location + ": No results produced at " + port.getSpec() + ".");
					} else {
						try {
							RepositoryAccessor repositoryAccessor = getRepositoryAccessor();
							location.setAccessor(repositoryAccessor);
							RepositoryManager.getInstance(repositoryAccessor).store(data, location, rootOperator);

						} catch (RepositoryException e) {
							throw new UserError(rootOperator, e, 315, location, e.getMessage());
						}
					}
				}
			}
		}
	}

	public void applyContextMacros() {
		for (Pair<String, String> macro : context.getMacros()) {
			getLogger().fine("Defining context macro: " + macro.getFirst() + " = " + macro.getSecond() + ".");
			getMacroHandler().addMacro(macro.getFirst(), macro.getSecond());
		}
	}

	/** Starts the process with no input. */
	public final IOContainer run() throws OperatorException {
		return run(new IOContainer());
	}

	/** Starts the process with the given log verbosity. */
	public final IOContainer run(int logVerbosity) throws OperatorException {
		return run(new IOContainer(), logVerbosity);
	}

	/** Starts the process with the given input. */
	public final IOContainer run(IOContainer input) throws OperatorException {
		return run(input, LogService.UNKNOWN_LEVEL);
	}

	/** Starts the process with the given input. The process uses the given log verbosity. */
	public final IOContainer run(IOContainer input, int logVerbosity) throws OperatorException {
		return run(input, logVerbosity, null);
	}

	/**
	 * Starts the process with the given input. The process uses a default log verbosity. The boolean flag indicates if
	 * some static initializations should be cleaned before the process is started. This should usually be true but it
	 * might be useful to set this to false if, for example, several process runs uses the same object visualizer which
	 * would have been cleaned otherwise.
	 */
	@Deprecated
	public final IOContainer run(IOContainer input, boolean unused) throws OperatorException {
		return run(input, LogService.UNKNOWN_LEVEL);
	}

	/**
	 * Starts the process with the given input. The process uses the given log verbosity. The boolean flag indicates if
	 * some static initializations should be cleaned before the process is started. This should usually be true but it
	 * might be useful to set this to false if, for example, several process runs uses the same object visualizer which
	 * would have been cleaned otherwise.
	 */
	@Deprecated
	public final IOContainer run(IOContainer input, int logVerbosity, boolean cleanUp) throws OperatorException {
		return run(input, logVerbosity, null);
	}

	/**
	 * Starts the process with the given input. The process uses the given log verbosity. The boolean flag indicates if
	 * some static initializations should be cleaned before the process is started. This should usually be true but it
	 * might be useful to set this to false if, for example, several process runs uses the same object visualizer which
	 * would have been cleaned otherwise.
	 * 
	 * Since the macros are cleaned then as well it is not possible to set macros to a process but with the given
	 * macroMap of this method.
	 */
	@Deprecated
	public final IOContainer run(IOContainer input, int logVerbosity, boolean cleanUp, Map<String, String> macroMap) throws OperatorException {
		return run(input, logVerbosity, macroMap);

	}

	public final IOContainer run(IOContainer input, int logVerbosity, Map<String, String> macroMap) throws OperatorException {
		return run(input, logVerbosity, macroMap, true);
	}

	/**
	 * Starts the process with the given input. The process uses the given log verbosity.
	 * 
	 * If input is not null, it is delivered to the input ports of the process. If it is null or empty,
	 * the input is read instead from the locations specified in the {@link ProcessContext}.
	 * 
	 * If input contains less IOObjects than are specified in the context, the remaining ones are
	 * read according to the context.
	 * 
	 * @param storeOutput Specifies if the output of the process should be saved. This is useful, if you
	 * 		embed a process using the Execute Process operator, and do not want to store the output as specified
	 * 		by the process context.
	 */
	public final IOContainer run(IOContainer input, int logVerbosity, Map<String, String> macroMap, boolean storeOutput) throws OperatorException {
		// fetching process name for logging
		String name = null;
		if (getProcessLocation() != null) {
			name = getProcessLocation().toString();
		}

		int myVerbosity = rootOperator.getParameterAsInt(ProcessRootOperator.PARAMETER_LOGVERBOSITY);
		if (logVerbosity == LogService.UNKNOWN_LEVEL) {
			logVerbosity = LogService.OFF;
		}
		logVerbosity = Math.min(logVerbosity, myVerbosity);
		getLogger().setLevel(WrapperLoggingHandler.LEVELS[logVerbosity]);
		String logFilename = rootOperator.getParameter(ProcessRootOperator.PARAMETER_LOGFILE);
		Handler logHandler = null;
		if (logFilename != null) {
			try {
				logHandler = new FileHandler(logFilename);
				logHandler.setFormatter(new SimpleFormatter());
				logHandler.setLevel(Level.ALL);
				getLogger().config("Logging process to file " + logFilename);
			} catch (Exception e) {
				getLogger().warning("Cannot create log file '" + logFilename + "': " + e);
			}
		}
		if (logHandler != null) {
			getLogger().addHandler(logHandler);
		}

		setProcessState(PROCESS_STATE_RUNNING);
		prepareRun(logVerbosity);

		long start = System.currentTimeMillis();
		if (name != null)
			getLogger().info("Process " + name + " starts");
		else
			getLogger().info("Process starts");
		getLogger().fine("Process:" + Tools.getLineSeparator() + getRootOperator().createProcessTree(3));

		// load data as specified in process context
		int firstInput = 0;
		if (input != null) {
			firstInput = input.getIOObjects().length;
		}
		loadInitialData(firstInput);

		// macros
		applyContextMacros();
		if (macroMap != null) {
			for (Map.Entry<String, String> entry : macroMap.entrySet()) {
				getMacroHandler().addMacro(entry.getKey(), entry.getValue());
			}
		}
		rootOperator.processStarts();

		try {
			UsageStatistics.getInstance().count(this, OperatorStatisticsValue.EXECUTION);
			if (input != null) {
				rootOperator.deliverInput(Arrays.asList(input.getIOObjects()));
			}
			rootOperator.execute();
			if (storeOutput) {
				saveResults();
			}
			IOContainer result = rootOperator.getResults();
			long end = System.currentTimeMillis();

			getLogger().fine("Process:" + Tools.getLineSeparator() + getRootOperator().createProcessTree(3));
			if (name != null)
				getLogger().info("Process " + name + " finished successfully after " + Tools.formatDuration(end - start));
			else
				getLogger().info("Process finished successfully after " + Tools.formatDuration(end - start));

			return result;
		} catch (OperatorException e) {
			if (e instanceof ProcessStoppedException) {
				Operator op = getOperator(((ProcessStoppedException) e).getOperatorName());
				UsageStatistics.getInstance().count(op, OperatorStatisticsValue.STOPPED);
			} else {
				UsageStatistics.getInstance().count(getCurrentOperator(), OperatorStatisticsValue.FAILURE);
				if (e instanceof UserError) {
					UsageStatistics.getInstance().count(((UserError) e).getOperator(), OperatorStatisticsValue.USER_ERROR);
				} else {
					UsageStatistics.getInstance().count(getCurrentOperator(), OperatorStatisticsValue.OPERATOR_EXCEPTION);
				}
			}
			throw e;
		} finally {
			stop();
			tearDown();
			if (logHandler != null) {
				getLogger().removeHandler(logHandler);
				logHandler.close();
			}
		}
	}

	/** This method is invoked after a process has finished. */
	private void tearDown() {
		try {
			rootOperator.processFinished();
		} catch (OperatorException e) {
			getLogger().log(Level.WARNING, "Problem during finishing the process: " + e.getMessage(), e);
		}

		// clean up
		//clearMacros();
		clearReportStreams();
		clearStorage();
		clearUnknownParameters();
		ResultService.close();
	}

	// ----------------------
	// Process IO
	// ----------------------

	public static Charset getEncoding(String encoding) {
		if (encoding == null) {
			encoding = ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_GENERAL_DEFAULT_ENCODING);
			if (encoding == null || encoding.trim().length() == 0) {
				encoding = RapidMiner.SYSTEM_ENCODING_NAME;
			}
		}

		Charset result = null;
		if (RapidMiner.SYSTEM_ENCODING_NAME.equals(encoding)) {
			result = Charset.defaultCharset();
		} else {
			try {
				result = Charset.forName(encoding);
			} catch (IllegalCharsetNameException e) {
				result = Charset.defaultCharset();
			} catch (UnsupportedCharsetException e) {
				result = Charset.defaultCharset();
			} catch (IllegalArgumentException e) {
				result = Charset.defaultCharset();
			}
		}
		return result;
	}

	/** Saves the process to the process file. */
	public void save() throws IOException {
		try {
			Process.checkIfSavable(this);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		if (processLocation != null) {
			this.isProcessConverted = false;
			processLocation.store(this, null);
			fireProcessStoredEvent(this);
		} else {
			throw new IOException("No process location is specified.");
		}
	}

	/** Saves the process to the given process file. */
	public void save(File file) throws IOException {
		try {
			Process.checkIfSavable(this);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		new FileProcessLocation(file).store(this, null);
		fireProcessStoredEvent(this);
	}

	/**
	 * Resolves the given filename against the directory containing the process file.
	 */
	public File resolveFileName(String name) {
		File absolute = new File(name);
		if (absolute.isAbsolute()) {
			return absolute;
		}
		if (processLocation instanceof FileProcessLocation) {
			File processFile = ((FileProcessLocation) processLocation).getFile();
			return Tools.getFile(processFile.getParentFile(), name);
		} else {
			String homeName;
			String resolvedir = System.getProperty("rapidminer.test.resolvedir");
			if (resolvedir == null) {
				homeName = System.getProperty("user.home");
			} else {
				homeName = resolvedir;
			}
			if (homeName != null) {
				File file = new File(new File(homeName), name);
				getLogger().warning("Process not attached to a file. Resolving against user directory: '" + file + "'.");
				return file;
			} else {
				getLogger().warning("Process not attached to a file. Trying abolute filename '" + name + "'.");
				return new File(name);
			}
		}
	}

	/** Reads the process setup from the given input stream. */
	public void readProcess(Reader in) throws XMLException, IOException {
		readProcess(in, null);
	}

	public void readProcess(Reader in, ProgressListener progressListener) throws XMLException, IOException {
		Map<String, Operator> nameMapBackup = operatorNameMap;
		operatorNameMap = new HashMap<String, Operator>(); // no invocation of clear (see below)

		if (progressListener != null) {
			progressListener.setTotal(120);
			progressListener.setCompleted(0);
		}
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(in));
			if (progressListener != null) {
				progressListener.setCompleted(20);
			}
			unknownParameterInformation.clear();
			XMLImporter xmlImporter = new XMLImporter(progressListener);
			xmlImporter.parse(document, this, unknownParameterInformation);

			nameMapBackup = operatorNameMap;
			rootOperator.clear(Port.CLEAR_ALL);
		} catch (javax.xml.parsers.ParserConfigurationException e) {
			throw new XMLException(e.toString(), e);
		} catch (SAXException e) {
			throw new XMLException("Cannot parse document: " + e.getMessage(), e);
		} finally {
			operatorNameMap = nameMapBackup; // if everything went fine -->
			// map = new map, if not -->
			// map = old map (backup)
			if (progressListener != null) {
				progressListener.complete();
			}
		}
	}

	/**
	 * Returns a &quot;name (i)&quot; if name is already in use. This new name should then be used as operator name.
	 */
	public String registerName(String name, Operator operator) {
		if (operatorNameMap.get(name) != null) {
			String baseName = name;
			int index = baseName.indexOf(" (");
			if (index >= 0) {
				baseName = baseName.substring(0, index);
			}
			int i = 2;
			while (operatorNameMap.get(baseName + " (" + i + ")") != null) {
				i++;
			}
			String newName = baseName + " (" + i + ")";
			operatorNameMap.put(newName, operator);
			return newName;
		} else {
			operatorNameMap.put(name, operator);
			return name;
		}
	}

	/** This method is used for unregistering a name from the operator name map. */
	public void unregisterName(String name) {
		operatorNameMap.remove(name);
	}

	public void notifyRenaming(String oldName, String newName) {
		rootOperator.notifyRenaming(oldName, newName);
	}

	@Override
	public String toString() {
		if (rootOperator == null)
			return "empty process";
		else
			return "Process:" + Tools.getLineSeparator() + rootOperator.getXML(true);
	}

	private final EventListenerList processSetupListeners = new EventListenerList();

	/** Delegates any changes in the ProcessContext to the root operator. */
	private final Observer<ProcessContext> delegatingContextObserver = new Observer<ProcessContext>() {

		@Override
		public void update(Observable<ProcessContext> observable, ProcessContext arg) {
			fireUpdate();
		}
	};
	private final Observer<Operator> delegatingOperatorObserver = new Observer<Operator>() {

		@Override
		public void update(Observable<Operator> observable, Operator arg) {
			fireUpdate();
		}
	};

	public void addProcessSetupListener(ProcessSetupListener listener) {
		processSetupListeners.add(ProcessSetupListener.class, listener);
	}

	public void removeProcessSetupListener(ProcessSetupListener listener) {
		processSetupListeners.remove(ProcessSetupListener.class, listener);
	}

	public void fireOperatorAdded(Operator operator) {
		for (ProcessSetupListener l : processSetupListeners.getListeners(ProcessSetupListener.class)) {
			l.operatorAdded(operator);
		}
	}

	public void fireOperatorChanged(Operator operator) {
		for (ProcessSetupListener l : processSetupListeners.getListeners(ProcessSetupListener.class)) {
			l.operatorChanged(operator);
		}
	}

	public void fireOperatorRemoved(Operator operator, int oldIndex, int oldIndexAmongEnabled) {
		for (ProcessSetupListener l : processSetupListeners.getListeners(ProcessSetupListener.class)) {
			l.operatorRemoved(operator, oldIndex, oldIndexAmongEnabled);
		}
	}

	public void fireExecutionOrderChanged(ExecutionUnit unit) {
		for (ProcessSetupListener l : processSetupListeners.getListeners(ProcessSetupListener.class)) {
			l.executionOrderChanged(unit);
		}
	}

	public ExecutionMode getExecutionMode() {
		return executionMode;
	}

	public void setExecutionMode(ExecutionMode mode) {
		this.executionMode = mode;
	}

	public DebugMode getDebugMode() {
		return debugMode;
	}

	public void setDebugMode(DebugMode mode) {
		this.debugMode = mode;
		if (mode == DebugMode.DEBUG_OFF) {
			getRootOperator().clear(Port.CLEAR_REAL_METADATA);
		}
	}

	/** Resolves a repository location relative to {@link #getRepositoryLocation()}. */
	public RepositoryLocation resolveRepositoryLocation(String loc) throws UserError, MalformedRepositoryLocationException {
		if (RepositoryLocation.isAbsolute(loc)) {
			RepositoryLocation repositoryLocation = new RepositoryLocation(loc);
			repositoryLocation.setAccessor(getRepositoryAccessor());
			return repositoryLocation;
		}
		RepositoryLocation repositoryLocation = getRepositoryLocation();
		if (repositoryLocation != null) {
			RepositoryLocation repositoryLocation2 = new RepositoryLocation(repositoryLocation.parent(), loc);
			repositoryLocation2.setAccessor(getRepositoryAccessor());
			return repositoryLocation2;
		} else {
			throw new UserError(null, 317, loc);
		}
	}

	/** Turns loc into a repository location relative to {@link #getRepositoryLocation()}. */
	public String makeRelativeRepositoryLocation(RepositoryLocation loc) {
		RepositoryLocation repositoryLocation = getRepositoryLocation();
		if (repositoryLocation != null) {
			return loc.makeRelative(repositoryLocation.parent());
		} else {
			return loc.getAbsoluteLocation();
		}
	}

	public void setContext(ProcessContext context) {
		if (this.context != null) {
			this.context.removeObserver(delegatingContextObserver);
		}
		this.context = context;
		this.context.addObserver(delegatingContextObserver, false);
		fireUpdate();
	}

	public ProcessContext getContext() {
		return context;
	}

	public void setImportMessage(String importMessage) {
		this.importMessage = importMessage;
	}

	/**
	 * This returns true if the process has been imported and ImportRules have been applied
	 * during importing. Since the backward compatibility is lost on save, one can warn by
	 * retrieving this value.
	 */
	public boolean isProcessConverted() {
		return isProcessConverted;
	}

	/**
	 * This sets whether the process is converted.
	 */
	public void setProcessConverted(boolean isProcessConverted) {
		this.isProcessConverted = isProcessConverted;
	}

	/**
	 * Returns some user readable messages generated during import by {@link XMLImporter}.
	 */
	public String getImportMessage() {
		return importMessage;
	}

	// process location (file/repository)

	/** Returns true iff either a file or a repository location is defined. */
	public boolean hasSaveDestination() {
		return processLocation != null;
	}

	/**
	 * Returns the current process file.
	 * 
	 * @deprecated Use {@link #getProcessFile()} instead
	 */
	@Deprecated
	public File getExperimentFile() {
		return getProcessFile();
	}

	/**
	 * Returns the current process file.
	 * 
	 * @deprecated Use {@link #getProcessLocation()}
	 */
	@Deprecated
	public File getProcessFile() {
		if (processLocation instanceof FileProcessLocation) {
			return ((FileProcessLocation) processLocation).getFile();
		} else {
			return null;
		}
	}

	/**
	 * Sets the process file. This file might be used for resolving relative filenames.
	 * 
	 * @deprecated Please use {@link #setProcessFile(File)} instead.
	 */
	@Deprecated
	public void setExperimentFile(File file) {
		setProcessLocation(new FileProcessLocation(file));
	}

	/** Sets the process file. This file might be used for resolving relative filenames. */
	public void setProcessFile(File file) {
		setProcessLocation(new FileProcessLocation(file));
	}

	public void setProcessLocation(ProcessLocation processLocation) {
		// keep process file version if same file, otherwise overwrite
		if (this.processLocation != null && !this.processLocation.equals(processLocation)) {
			this.isProcessConverted = false;
			getLogger().info("Decoupling process from location " + this.processLocation + ". Process is now associated with file " + processLocation + ".");
		}
		this.processLocation = processLocation;
		fireUpdate();
	}

	public ProcessLocation getProcessLocation() {
		return this.processLocation;
	}

	public RepositoryLocation getRepositoryLocation() {
		if (processLocation instanceof RepositoryProcessLocation) {
			return ((RepositoryProcessLocation) processLocation).getRepositoryLocation();
		} else {
			return null;
		}
	}

	/**
	 * Can be called by GUI components if visual representation or any other state not known to the process itself has
	 * changed.
	 */
	public void updateNotify() {
		fireUpdate(this);
	}

	public RepositoryAccessor getRepositoryAccessor() {
		return repositoryAccessor;
	}

	public void setRepositoryAccessor(RepositoryAccessor repositoryAccessor) {
		this.repositoryAccessor = repositoryAccessor;
	}

	public Annotations getAnnotations() {
		return annotations;
	}
	
	/** Checks weather the process can be saved as is. Throws an Exception if the Process should not be saved. **/
	public static void checkIfSavable(Process process) throws Exception {
		for (Operator operator : process.getAllOperators()) {
			if (operator instanceof DummyOperator) {
				throw new Exception("The process contains dummy operators. Remove all dummy operators or install all missing extensions in order to save the process.");
			}
		}
	}

}
