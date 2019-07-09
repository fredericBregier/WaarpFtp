/**
 * This file is part of Waarp Project.
 * <p>
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author tags. See the COPYRIGHT.txt in the
 * distribution for a full listing of individual contributors.
 * <p>
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Waarp . If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.ftp.core.data;

import org.waarp.common.command.exception.CommandAbstractException;
import org.waarp.common.logging.WaarpLogger;
import org.waarp.common.logging.WaarpLoggerFactory;
import org.waarp.ftp.core.command.FtpCommandCode;
import org.waarp.ftp.core.exception.FtpNoFileException;
import org.waarp.ftp.core.file.FtpFile;

import java.util.List;

/**
 * Class that owns one transfer to be run
 *
 * @author Frederic Bregier
 *
 */
public class FtpTransfer {
    private static final WaarpLogger logger = WaarpLoggerFactory.getLogger(FtpTransfer.class);

    /**
     * The command to execute
     */
    private final FtpCommandCode command;

    /**
     * The information (list) on which the command was executed
     */
    private final List<String> info;
    /**
     * Current Ftp FileInterface
     */
    private final FtpFile currentFile;
    /**
     * The original path on which the command was executed
     */
    private String path = null;
    /**
     * The status
     */
    private boolean status = false;

    /**
     * @param command
     * @param fileOrInfo
     * @param path
     */
    public FtpTransfer(FtpCommandCode command, List<String> fileOrInfo,
                       String path) {
        this.command = command;
        info = fileOrInfo;
        this.path = path;
        currentFile = null;
    }

    /**
     * @param command
     * @param file
     */
    public FtpTransfer(FtpCommandCode command, FtpFile file) {
        this.command = command;
        currentFile = file;
        try {
            path = file.getFile();
        } catch (CommandAbstractException e) {
        }
        info = null;
    }

    /**
     * @return the command
     */
    public FtpCommandCode getCommand() {
        return command;
    }

    /**
     * @return the file
     * @throws FtpNoFileException
     */
    public FtpFile getFtpFile() throws FtpNoFileException {
        if (currentFile == null) {
            throw new FtpNoFileException("No file associated with the transfer");
        }
        return currentFile;
    }

    /**
     * @return the Info
     */
    public List<String> getInfo() {
        return info;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the status
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(boolean status) {
        if (!status) {
            logger.debug("Status false", new Exception("Trace only exception"));
        }
        this.status = status;
    }

    /**
     *
     */
    @Override
    public String toString() {
        return command.name() + " " + path;
    }
}
