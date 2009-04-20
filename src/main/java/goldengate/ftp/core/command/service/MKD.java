/**
 * Copyright 2009, Frederic Bregier, and individual contributors
 * by the @author tags. See the COPYRIGHT.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package goldengate.ftp.core.command.service;

import goldengate.common.command.ReplyCode;
import goldengate.common.command.exception.CommandAbstractException;
import goldengate.common.command.exception.Reply501Exception;
import goldengate.ftp.core.command.AbstractCommand;

/**
 * MKD command
 *
 * @author Frederic Bregier
 *
 */
public class MKD extends AbstractCommand {

    /*
     * (non-Javadoc)
     *
     * @see goldengate.ftp.core.command.AbstractCommand#exec()
     */
    @Override
    public void exec() throws CommandAbstractException {
        // First Check if any argument
        if (!hasArg()) {
            throw new Reply501Exception("Need a path as argument");
        }
        String path = getArg();
        String newpath = getSession().getDir().mkdir(path);
        getSession().setReplyCode(ReplyCode.REPLY_257_PATHNAME_CREATED,
                "\"" + newpath + "\" is created");
    }

}
