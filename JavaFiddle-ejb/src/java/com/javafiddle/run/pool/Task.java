package com.javafiddle.run.pool;

import com.javafiddle.run.Launcher;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import java.util.Date;

public class Task extends Thread implements Serializable {

    private String id = null;
    private TaskStatus status = TaskStatus.STARTING;
    private TaskType type = null;
    private Date startDate = null;
    private Date startCompilationDate = null;
    private Date endDate = null;
    private Launcher process;

    public Task(TaskType type, Launcher process) {
        this.type = type;
        this.process = process;
        this.startDate = new Date();
    }

    @Override
    public void run() {
        startCompilationDate = new Date();
        status = TaskStatus.LAUNCHED;
        try {
            process.run();
        } catch (Exception e) {
            status = TaskStatus.ERROR;
        }
        finally {
            process.waitFor();
            endDate = new Date();
            status = process.getExitCode() == 0 ? TaskStatus.COMPLETED : TaskStatus.ERROR;
        }
    }

    public void kill() {
        try {
            process.destroy();
        } catch(Exception e) {
            status = TaskStatus.ERROR;
        } finally {
            process.waitFor();
            status = process.getExitCode() == 0 ? TaskStatus.COMPLETED : TaskStatus.ERROR;
       }
    }

    public String getOutputStream() {
        return process.getOutputStream();
    }

    public InputStream getErrorStream() {
        return process.getErrorStream();
    }
    
    public Boolean isCompleted() {
        if(status == TaskStatus.COMPLETED)
            return true;
        return false;
    }
    
    public Boolean isError() {
        if(status == TaskStatus.ERROR)
            return true;
        return false;
    }

    public Boolean streamIsEmpty() {
        return process.streamIsEmpty();
    }
    
    public Date getStartTime() {
        return startCompilationDate;
    }
    
    public Date getEndTime() {
        return endDate;
    }

    public OutputStream getInputStream() {
        return process.getInputStream();
    }

    public Launcher getProcess() {
        return process;
    }
    
    public TaskType getType() {
        return type;
    }
    
    public Integer getPid() {
        return process.getPid();
    }

    public void addToOutput(String line) {
        process.addToOutput(line);
    }

}
