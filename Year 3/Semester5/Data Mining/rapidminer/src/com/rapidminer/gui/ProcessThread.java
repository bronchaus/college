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
package com.rapidminer.gui;

import java.util.logging.Level;

import com.rapidminer.NoBugError;
import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ProcessStoppedException;
import com.rapidminer.operator.UserError;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.usagestats.OperatorStatisticsValue;
import com.rapidminer.tools.usagestats.UsageStatistics;

/**
 * A Thread for running an process in the RapidMinerGUI. This thread is necessary in order to keep the GUI running (and
 * working). Please note that this class can only be used from a running RapidMiner GUI since several dependencies to
 * the class {@link RapidMinerGUI} and {@link MainFrame} exist. If you want to perform an process in its own thread from
 * your own program simply use a Java Thread peforming the method process.run() in its run()-method.
 * 
 * @author Ingo Mierswa, Simon Fischer Exp $
 */
public class ProcessThread extends Thread {// implements ProcessListener {

    private Process process;

    public ProcessThread(Process process) {
        super("ProcessThread");
        this.process = process;
    }

    @Override
    public void run() {
        // this.process.getRootOperator().addProcessListener(this);
        try {
            IOContainer results = process.run();
            beep("success");
            process.getRootOperator().sendEmail(results, null);
            RapidMinerGUI.getMainFrame().processEnded(process, results);
        } catch (ProcessStoppedException ex) {
            // beep("error");
            process.getLogger().info(ex.getMessage());
            // here the process ended method is not called ! let the thread finish the
            // current operator and send no events to the main frame...
            // also no beep...
        } catch (Throwable e) {
            // TODO: We shouldn't catch JVM crashes we can't fix anyway...
            // } catch (Exception e) {

            if (!(e instanceof OperatorException)) { // otherwise it was already counted
                UsageStatistics.getInstance().count(process.getCurrentOperator(), OperatorStatisticsValue.FAILURE);
                UsageStatistics.getInstance().count(process.getCurrentOperator(), OperatorStatisticsValue.RUNTIME_EXCEPTION);
            }

            beep("error");
            String debugProperty = ParameterService.getParameterValue(RapidMiner.PROPERTY_RAPIDMINER_GENERAL_DEBUGMODE);
            boolean debugMode = Tools.booleanValue(debugProperty, false);
            String message = e.getMessage();
            if (!debugMode) {
                if (e instanceof RuntimeException) {
                    if (e.getMessage() != null)
                        message = "operator cannot be executed (" + e.getMessage() + "). Check the log messages...";
                    else
                        message = "operator cannot be executed. Check the log messages...";
                }
            }
            process.getLogger().log(Level.SEVERE, "Process failed: " + message, e);
            process.getLogger().log(Level.SEVERE, "Here: " + process.getRootOperator().createMarkedProcessTree(10, "==>", process.getCurrentOperator()));

            try {
                process.getRootOperator().sendEmail(null, e);
            } catch (UndefinedParameterError ex) {
                // cannot happen
                process.getLogger().log(Level.WARNING, "Problems during sending result mail: " + ex.getMessage(), ex);
            }

            if (e instanceof OutOfMemoryError) { // out of memory --> give memory hint
                SwingTools.showVerySimpleErrorMessage("proc_failed_out_of_mem");
            } else if (e instanceof NoBugError) { // no bug? Show nice error screen (user error infos)...
                if (e instanceof UserError) {
                    UserError userError = (UserError) e;
                    SwingTools.showFinalErrorMessage("process_failed_user_error", e, debugMode,new Object[]{userError.getMessage(), userError.getDetails()});
                } else {
                    SwingTools.showFinalErrorMessage("process_failed_simple", e, debugMode, new Object[] {});
                }
            } else {
                if (debugMode) {
                    SwingTools.showFinalErrorMessage("process_failed_simple", e, true, new Object[] {});
                } else {
                    // perform process check. No bug report if errors...
                    if (e instanceof NullPointerException || e instanceof ArrayIndexOutOfBoundsException) {
                        LogService.getRoot().log(Level.SEVERE, e.toString(), e);
                        SwingTools.showVerySimpleErrorMessage("proc_failed_without_obv_reason");
                    } else {
                        SwingTools.showSimpleErrorMessage("process_failed_simple", e, false, new Object[] {});
                    }
                }
            }
            RapidMinerGUI.getMainFrame().processEnded(this.process, null);
        } finally {
            // this.process.getRootOperator().removeProcessListener(this);
            if (process.getProcessState() != Process.PROCESS_STATE_STOPPED) {
                process.stop();
            }
            this.process = null;
        }
    }

    public static void beep(String reason) {
        if (Tools.booleanValue(ParameterService.getParameterValue("rapidminer.gui.beep." + reason), false)) {
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
    }

    public void stopProcess() {
        if (process != null) {
            this.process.stop();
            this.interrupt();
        }
    }

    public void pauseProcess() {
        if (process != null) {
            this.process.pause();
        }
    }

    @Override
    public String toString() {
        return "ProcessThread (" + process.getProcessLocation() + ")";
    }
}
