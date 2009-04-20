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
package goldengate.ftp.core.session;

import goldengate.common.command.CommandInterface;
import goldengate.common.command.ReplyCode;
import goldengate.common.command.exception.CommandAbstractException;
import goldengate.common.file.AuthInterface;
import goldengate.common.file.DirInterface;
import goldengate.common.file.FileParameterInterface;
import goldengate.common.file.Restart;
import goldengate.common.file.SessionInterface;
import goldengate.ftp.core.command.AbstractCommand;
import goldengate.ftp.core.config.FtpConfiguration;
import goldengate.ftp.core.control.BusinessHandler;
import goldengate.ftp.core.control.NetworkHandler;
import goldengate.ftp.core.data.FtpDataAsyncConn;

import org.jboss.netty.channel.Channel;

/**
 * Main class that stores any information that must be accessible from anywhere
 * during the connection of one user.
 *
 * @author Frederic Bregier
 *
 */
public class FtpSession implements SessionInterface {
    /**
     * Business Handler
     */
    private BusinessHandler businessHandler = null;

    /**
     * Associated global configuration
     */
    private FtpConfiguration configuration = null;

    /**
     * Associated Binary connection
     */
    private FtpDataAsyncConn dataConn = null;

    /**
     * Ftp Authentication
     */
    private AuthInterface ftpAuth = null;

    /**
     * Ftp DirInterface configuration and access
     */
    private DirInterface ftpDir = null;

    /**
     * Previous Command
     */
    private AbstractCommand previousCommand = null;

    /**
     * Current Command
     */
    private AbstractCommand currentCommand = null;

    /**
     * Associated Reply Code
     */
    private ReplyCode replyCode = null;

    /**
     * Real text for answer
     */
    private String answer = null;

    /**
     * Current Restart information
     */
    private Restart restart = null;

    /**
     * Is the control ready to accept command
     */
    private boolean isReady = false;

    /**
     * Constructor
     *
     * @param configuration
     * @param handler
     */
    public FtpSession(FtpConfiguration configuration, BusinessHandler handler) {
        this.configuration = configuration;
        businessHandler = handler;
        isReady = false;
    }

    /**
     * @return the businessHandler
     */
    public BusinessHandler getBusinessHandler() {
        return businessHandler;
    }

    /**
     * Get the configuration
     *
     * @return the configuration
     */
    public FtpConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public DirInterface getDir() {
        return ftpDir;
    }

    /**
     * @return the Data Connection
     */
    public FtpDataAsyncConn getDataConn() {
        return dataConn;
    }

    @Override
    public AuthInterface getAuth() {
        return ftpAuth;
    }

    @Override
    public Restart getRestart() {
        return restart;
    }

    /**
     * This function is called when the Command Channel is connected (from
     * channelConnected of the NetworkHandler)
     */
    public void setControlConnected() {
        dataConn = new FtpDataAsyncConn(this);
        // AuthInterface must be done before FtpFile
        ftpAuth = businessHandler.getBusinessNewAuth();
        ftpDir = businessHandler.getBusinessNewDir();
        restart = businessHandler.getBusinessNewRestart();
    }

    /**
     * @return the Control channel
     */
    public Channel getControlChannel() {
        return getNetworkHandler().getControlChannel();
    }

    /**
     *
     * @return The network handler associated with control
     */
    public NetworkHandler getNetworkHandler() {
        if (businessHandler != null) {
            return businessHandler.getNetworkHandler();
        }
        return null;
    }

    /**
     * Set the new current command
     *
     * @param command
     */
    public void setNextCommand(CommandInterface command) {
        previousCommand = currentCommand;
        currentCommand = (AbstractCommand) command;
    }

    /**
     * @return the currentCommand
     */
    public AbstractCommand getCurrentCommand() {
        return currentCommand;
    }

    /**
     * @return the previousCommand
     */
    public AbstractCommand getPreviousCommand() {
        return previousCommand;
    }

    /**
     * Set the previous command as the new current command (used after a
     * incorrect sequence of commands or unknown command)
     *
     */
    public void setPreviousAsCurrentCommand() {
        currentCommand = previousCommand;
    }

    /**
     * @return the answer
     */
    public String getAnswer() {
        if (answer == null) {
            answer = replyCode.getMesg();
        }
        return answer;
    }

    /**
     * @param replyCode
     *            the replyCode to set
     * @param answer
     */
    public void setReplyCode(ReplyCode replyCode, String answer) {
        this.replyCode = replyCode;
        if (answer != null) {
            this.answer = ReplyCode.getFinalMsg(replyCode.getCode(), answer);
        } else {
            this.answer = replyCode.getMesg();
        }
    }

    /**
     * @param exception
     */
    public void setReplyCode(CommandAbstractException exception) {
        this.setReplyCode(exception.code, exception.message);
    }

    /**
     * Set Exit code after an error
     *
     * @param answer
     */
    public void setExitErrorCode(String answer) {
        this
                .setReplyCode(
                        ReplyCode.REPLY_421_SERVICE_NOT_AVAILABLE_CLOSING_CONTROL_CONNECTION,
                        answer);
    }

    /**
     * Set Exit normal code
     *
     * @param answer
     */
    public void setExitNormalCode(String answer) {
        this.setReplyCode(ReplyCode.REPLY_221_CLOSING_CONTROL_CONNECTION,
                answer);
    }

    /**
     * @return the replyCode
     */
    public ReplyCode getReplyCode() {
        return replyCode;
    }

    @Override
    public void clean() {
        if (dataConn != null) {
            dataConn.clear();
            dataConn = null;
        }
        if (ftpDir != null) {
            ftpDir.clear();
            ftpDir = null;
        }
        if (ftpAuth != null) {
            ftpAuth.clean();
            ftpAuth = null;
        }
        businessHandler = null;
        configuration = null;
        previousCommand = null;
        currentCommand = null;
        replyCode = null;
        answer = null;
        isReady = false;
    }

    /**
     * @return True if the Control is ready to accept command
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * @param isReady
     *            the isReady to set
     */
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    @Override
    public String toString() {
        String mesg = "FtpSession: ";
        if (currentCommand != null) {
            mesg += "CMD: " + currentCommand.getCommand() + " " +
                    currentCommand.getArg() + " ";
        }
        if (replyCode != null) {
            mesg += "Reply: " +
                    (answer != null? answer : replyCode
                            .getMesg()) + " ";
        }
        if (dataConn != null) {
            mesg += dataConn.toString();
        }
        if (ftpDir != null) {
            try {
                mesg += "PWD: " + ftpDir.getPwd();
            } catch (CommandAbstractException e) {
            }
        }
        return mesg + "\n";
    }

    @Override
    public int getBlockSize() {
        return configuration.BLOCKSIZE;
    }

    @Override
    public FileParameterInterface getFileParameter() {
        return configuration.getFileParameter();
    }
}