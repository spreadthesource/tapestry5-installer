package com.spreadthesource.tapestry.installer.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigurationManagerImpl implements ConfigurationManager
{
    private List<ConfigurationTask> tasks;

    private int tasksSize;

    private int index;

    public ConfigurationManagerImpl(List<ConfigurationTask> tasks)
    {
        this.tasks = tasks;
        this.tasksSize = this.tasks.size();
        this.index = 0;
    }

    public boolean hasNext()
    {
        return (index < (tasksSize - 1));
    }

    public boolean hasPrevious()
    {
        return (index > 0);
    }

    public ConfigurationTask getCurrentTask()
    {
        return tasks.get(index);
    }

    public ConfigurationTask getPreviousTask()
    {
        return tasks.get(index - 1);
    }

    public ConfigurationTask getNextTask()
    {
        return tasks.get(index + 1);
    }

    public void configure()
    {
        ConfigurationTask task = getCurrentTask();
        task.run();

        if (!task.isConfigured()) { throw new RuntimeException(
                "Failed to run a configuration task, aborting configuration process. Task failed: "
                        + task.getClass().getSimpleName()); }

        if (hasNext())
        {
            index++;

            if (getCurrentTask().getStartPage() == null)
            {
                configure();
            }
        }
    }

    public void rollback()
    {
        ConfigurationTask task = getCurrentTask();

        if (task.isConfigured())
        {
            task.rollback();

        }

        if (task.isConfigured()) { throw new RuntimeException(
                "Failed to rollback a configuration task, aborting configuration process. Task failed: "
                        + task.getClass().getSimpleName()); }

        if (hasPrevious())
        {
            index--;

            if (getCurrentTask().getStartPage() == null)
            {
                rollback();
            }
        }

    }

    public void configureAll()
    {
        for (ConfigurationTask task : tasks)
        {
            task.run();

            if (!task.isConfigured()) throw new RuntimeException(
                    "Failed to run a configuration task, aborting configuration process. Task failed: "
                            + task.getClass().getSimpleName());
            
            if (hasNext()) index++;
        }
    }

    public void rollbackAll()
    {
        List<ConfigurationTask> revertedTasks = new ArrayList<ConfigurationTask>();
        revertedTasks.addAll(tasks);
        Collections.reverse(revertedTasks);
        index = revertedTasks.size() - 1;

        for (ConfigurationTask task : revertedTasks)
        {
            if (task.isConfigured())
            {
                task.rollback();
                if (index > 0) index--;
            }
        }
    }
}
