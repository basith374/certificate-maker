/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author bazi
 */
public class ImageWriterService {

    private final List<Task> tasks = new ArrayList<>();
    
    private final ProgressBar progressBar;
    private final Label statusLabel;

    private IntegerProperty pendingTasks = new SimpleIntegerProperty();
    
    private ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });
    
    private final TaskMonitor taskMonitor = new TaskMonitor();
    
    private final Window window;    

    public ImageWriterService(final Window window) {
        this.window = window;
        
        this.progressBar = window.getProgressBar();
        this.statusLabel = window.getStatusLabel();
        
        // auto-hide progressbar on idle task
        taskMonitor.idleProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable o) {
                if(taskMonitor.idleProperty().get()) {
                    window.removeProgressBar();
                } else {
                    window.addProgressBar();
                }
            }
        });
        
        // bind progressbar to taskmonitor
        progressBar.progressProperty().bind(taskMonitor.currentTaskProgressProperty());
    }
    
//    public void submit(Task task) {
//        exec.submit(task);
//        threads.add(task);
//    }
//    
    
    public Task<Void> createWorker(final Image certificateImage, final HashMap<CertificateField, String> fields, final File saveFile) {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                BufferedImage bufferedImage = ImageUtils.createBufferedImage(certificateImage, fields); // IMPORTANT
                try (FileOutputStream fos = new FileOutputStream(saveFile); ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
                    ImageWriter writer = writers.next(); 
                    writer.setOutput(ios);
                    writer.addIIOWriteProgressListener(new IIOWriteProgressListener() {
                        @Override public void imageStarted(ImageWriter source, int imageIndex) { updateMessage("Saving " + saveFile.getAbsolutePath()); }
                        // Only this method is used to send progress to task
                        @Override public void imageProgress(ImageWriter source, float percentageDone) { updateProgress(percentageDone, 100); }
                        @Override public void imageComplete(ImageWriter source) {}
                        @Override public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) {}
                        @Override public void thumbnailProgress(ImageWriter source, float percentageDone) {}
                        @Override public void thumbnailComplete(ImageWriter source) {}
                        @Override public void writeAborted(ImageWriter source) {}
                    });
                    writer.write(bufferedImage);
                    writer.dispose();
                }
                return null;
            }
        };        
    }

    void takeWork(Image certificateImage, HashMap<CertificateField, String> fields, File saveFile) {
        Task task = createWorker(certificateImage, fields, saveFile);
        task.setOnSucceeded(new EventHandler() {
            @Override
            public void handle(Event t) {
                pendingTasks.set(pendingTasks.get() - 1);
                if(pendingTasks.get() > 0) {
                    statusLabel.setText("Tasks : " + pendingTasks.get());
                } else {
                    statusLabel.setText("All tasks done.");
                }
            }
        });
        pendingTasks.set(pendingTasks.get() + 1);
        statusLabel.setText("Tasks : " + pendingTasks.get());
//        progressBar.progressProperty().bind(task.progressProperty());
        taskMonitor.monitor(task);
        exec.submit(task);
    }
    
    class TaskMonitor {
        private final ReadOnlyObjectWrapper<Task> currentTask = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyStringWrapper currentTaskName = new ReadOnlyStringWrapper();
        private final ReadOnlyDoubleWrapper currentTaskProgress = new ReadOnlyDoubleWrapper();
        private final ReadOnlyBooleanWrapper idle = new ReadOnlyBooleanWrapper(true);
        
        public void monitor(final Task task) {
            task.stateProperty().addListener(new ChangeListener<Task.State>() {
                @Override
                public void changed(ObservableValue<? extends Task.State> observableValue, Task.State oldState, Task.State state) {
                    switch(state) {
                        case RUNNING:
                            currentTask.set(task);
                            currentTaskProgress.unbind();
                            currentTaskProgress.set(task.progressProperty().get());
                            currentTaskProgress.bind(task.progressProperty());
//                            currentTaskName.set(task.getNameProperty());
                            idle.set(false);
                            break;
                        case SUCCEEDED:
                        case CANCELLED:
                        case FAILED:
                            task.stateProperty().removeListener(this);
                            idle.set(true);
                            break;
                    }
                    
                }
            });
        }
        
        public void monitor(final Task...tasks) {
            for(Task task : tasks) {
                monitor(task);
            }
        }

        public ReadOnlyObjectProperty<Task> currentTaskProperty() {
            return currentTask.getReadOnlyProperty();
        }

//        public ReadOnlyStringProperty currentTaskNameProperty() {
//            return currentTaskName.getReadOnlyProperty();
//        }

        public ReadOnlyDoubleProperty currentTaskProgressProperty() {
            return currentTaskProgress.getReadOnlyProperty();
        }

        public ReadOnlyBooleanProperty idleProperty() {
            return idle.getReadOnlyProperty();
        }        
        
    }
    
}
