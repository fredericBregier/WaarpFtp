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
package org.waarp.ftp.core.command.service;

import org.waarp.common.command.ReplyCode;
import org.waarp.common.command.exception.CommandAbstractException;
import org.waarp.common.command.exception.Reply501Exception;
import org.waarp.common.command.exception.Reply550Exception;
import org.waarp.ftp.core.command.AbstractCommand;

/**
 * RMD command
 *
 * @author Frederic Bregier
 */
public class RMD extends AbstractCommand {
    @Override
    public void exec() throws Reply550Exception, CommandAbstractException {
        // First Check if any argument
        if (!hasArg()) {
            throw new Reply501Exception("Need a path as argument");
        }
        String path = getArg();
        String pastdir = getSession().getDir().rmdir(path);
        getSession().setReplyCode(
                ReplyCode.REPLY_250_REQUESTED_FILE_ACTION_OKAY,
                "\"" + pastdir + "\" is deleted");
    }

}