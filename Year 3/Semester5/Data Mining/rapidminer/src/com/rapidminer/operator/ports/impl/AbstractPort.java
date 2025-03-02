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
package com.rapidminer.operator.ports.impl;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.rapidminer.gui.renderer.RendererService;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.Port;
import com.rapidminer.operator.ports.Ports;
import com.rapidminer.operator.ports.metadata.MetaDataError;
import com.rapidminer.operator.ports.quickfix.QuickFix;
import com.rapidminer.tools.AbstractObservable;


/** Implemented by keeping a weak reference to the data that can be cleared at any time
 *  by the garbage collector.
 * 
 *  In addition to the week reference, this class also keeps
 *  a hard reference to the data, freeing it when calling {@link #freeMemory()}.
 * 
 * @author Simon Fischer
 *
 */
public abstract class AbstractPort extends AbstractObservable<Port> implements Port  {

    private final List<MetaDataError> errorList = new LinkedList<MetaDataError>();
    private final Ports<? extends Port> ports;

    private String name;

    private SoftReference<IOObject> weakDataReference;

    private IOObject hardDataReference;

    private final boolean simulatesStack;
    private boolean locked = false;

    protected AbstractPort(Ports<? extends Port> owner, String name, boolean simulatesStack) {
        this.name = name;
        this.ports = owner;
        this.simulatesStack = simulatesStack;
    }

    protected final void setData(IOObject object) {
        this.weakDataReference = new SoftReference<IOObject>(object);
        this.hardDataReference = object;
    }

    @Deprecated
    @Override    
    public <T extends IOObject> T getData() throws UserError {
        T data = this.<T>getDataOrNull();
        if (data == null) {
            throw new UserError(getPorts().getOwner().getOperator(), 149, getSpec() + (isConnected() ? " (connected)" : " (disconnected)"));
        } else {
            return data;
        }
    }

    @Override
    public IOObject getAnyDataOrNull() {
        if (hardDataReference != null) {
            return hardDataReference;
        } else {
            return this.weakDataReference != null ? this.weakDataReference.get() : null;
        }
    }

    @Override
    public <T extends IOObject> T getData(Class<T> desiredClass) throws UserError {
    	IOObject data = getAnyDataOrNull();
    	if (data == null) {
            throw new UserError(getPorts().getOwner().getOperator(), 149, getSpec() + (isConnected() ? " (connected)" : " (disconnected)"));
    	} else if (desiredClass.isAssignableFrom(data.getClass())) {
            return desiredClass.cast(data);
        } else {
            throw new UserError(getPorts().getOwner().getOperator(), 156, RendererService.getName(data.getClass()), this.getName(), RendererService.getName(desiredClass));
        }
    }

    @Override
    public <T extends IOObject> T getDataOrNull(Class<T> desiredClass) throws UserError {
        IOObject data = getAnyDataOrNull();
        if (data == null) {
        	return null;
        } else if (desiredClass.isAssignableFrom(data.getClass())) {
            return desiredClass.cast(data);
        } else {
            throw new UserError(getPorts().getOwner().getOperator(), 156, RendererService.getName(data.getClass()), this.getName(), RendererService.getName(desiredClass));
        }
    }
        
    @SuppressWarnings("unchecked")
	@Deprecated
    @Override
    public <T extends IOObject> T getDataOrNull() throws UserError {
        IOObject data = getAnyDataOrNull();
        return (T) data;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getSpec();
    }

    @Override
    public Ports<? extends Port> getPorts() {
        return ports;
    }

    @Override
    public String getShortName() {
        if (name.length() > 3) {
            return name.substring(0, 3);
        } else {
            return name;
        }
    }

    /** Don't use this method. Use {@link Ports#renamePort(Port,String)}. */
    protected void setName(String newName) {
        this.name = newName;
    }

    @Override
    public void addError(MetaDataError metaDataError) {
        errorList.add(metaDataError);
    }

    @Override
    public Collection<MetaDataError> getErrors() {
        return Collections.unmodifiableCollection(errorList);
    }

    @Override
    public void clear(int clearFlags) {
        if ((clearFlags & CLEAR_META_DATA_ERRORS) > 0) {
            this.errorList.clear();
        }
        if ((clearFlags & CLEAR_DATA) > 0) {
            this.weakDataReference = null;
            this.hardDataReference = null;
        }
    }

    @Override
    public List<QuickFix> collectQuickFixes() {
        List<QuickFix> fixes = new LinkedList<QuickFix>();
        for (MetaDataError error : getErrors()) {
            fixes.addAll(error.getQuickFixes());
        }
        Collections.sort(fixes);
        return fixes;
    }

    @Override
    public String getSpec() {
        if (getPorts() != null) {
            return getPorts().getOwner().getOperator().getName() + "." + getName();
        } else {
            return "DUMMY."+getName();
        }
    }

    @Override
    public boolean simulatesStack() {
        return simulatesStack;
    }

    @Override
    public boolean isLocked()  {
        return locked;
    }

    @Override
    public void unlock()  {
        this.locked = false;
    }

    @Override
    public void lock() {
        this.locked = true;
    }

    /** Releases of the hard reference. */
    @Override
    public void freeMemory() {
        this.hardDataReference = null;
    }
}
