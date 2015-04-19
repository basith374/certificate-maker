/*
 * Copyright BCZ Inc. 2015.
 * This file is part of Certificate Maker.
 *
 * Certificate Maker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Certificate Maker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Certificate Maker.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bluecipherz.certificatemaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
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
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

/**
 *
 * @author bazi
 */
public class ImageWriterService {

    private final ProgressBar progressBar;
    private final Label statusLabel;

    
//    private final ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactory() {
//        @Override
//        public Thread newThread(Runnable r) {
//            Thread t = new Thread(r);
//            t.setDaemon(true);
//            return t;
//        }
//    });
    
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    
    private final TaskMonitor taskMonitor = new TaskMonitor();
    
    private ImageWriteOrder queue;
    
    private Image certificateImage;  
    
    private boolean a3output = UserDataManager.isA3Output();
    private String defaultExtension = UserDataManager.getDefaultImageFormat();

    //    private boolean dual = true; // for test
    public Image getCertificateImage() {
        return certificateImage;
    }

    public void setCertificateImage(Image certificateImage) {
        this.certificateImage = certificateImage;
    }


    public void setA3Output(boolean dual) {
        this.a3output = dual;
    }
    
    private final CertificateUtils certificateUtils = new CertificateUtils();

    public void setDefaultExtension(String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }

    /**
     * Image Writer Service literally means what it says. this class is given work
     * orders it creates the output.
     * @param window
     * @param certificateImage 
     */
    public ImageWriterService(final Window window) {
        
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
        
        taskMonitor.pendingTasksProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                if(newVal.intValue() > 0) {
                    statusLabel.setText("Tasks : " + newVal.intValue());
                } else {
                    statusLabel.setText("All tasks done.");
                }
            }
        });
    }
    
    public Task<Void> createOutputWorker(final BufferedImage bufferedImage, final File saveFile) {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                try (FileOutputStream fos = new FileOutputStream(saveFile); ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
                    Debugger.log("image writers format " + defaultExtension); // debug
                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(defaultExtension);
                    ImageWriter writer = writers.next(); 
                    // lossless compression
//                    ImageWriteParam iwp = writer.getDefaultWriteParam();
//                    if("jpg".equalsIgnoreCase(defaultExtension)) {
//                        Debugger.log("JPEG Lossless compression...");
//                        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//                        iwp.setCompressionType("JPEG-LS");
//                    }
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
//                    writer.write(null, new IIOImage(bufferedImage, null, null), iwp); // jpeg
                    Debugger.log("disposing write sequence");
                    writer.dispose();
                }
                return null;
            }
        };
    }
    
    // TODO seperate workers for jpg and png
    public Task<Void> createJPGWorker(final BufferedImage bufferedImage, final File saveFile) {
        return new Task() {
            @Override
            protected Void call() throws Exception {
                try (FileOutputStream fos = new FileOutputStream(saveFile); ImageOutputStream ios = ImageIO.createImageOutputStream(fos)) {
                    Debugger.log("image writers format " + defaultExtension); // debug
                    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
                    ImageWriter writer = writers.next(); 
                    // lossless compression
                    ImageWriteParam iwp = writer.getDefaultWriteParam();
                    if("jpg".equalsIgnoreCase(defaultExtension)) {
                        Debugger.log("JPEG Lossless compression...");
                        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        iwp.setCompressionType("JPEG-LS");
//                        iwp.setCompressionType();
                    }
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
//                    writer.write(null, new IIOImage(bufferedImage, null, null), iwp); // jpeg
                    Debugger.log("disposing write sequence");
                    writer.dispose();
                }
                return null;
            }
        };
    }

    public void takeWork(BufferedImage bi, File file){
        Task<Void> task = createOutputWorker(bi, fixDefaultExtension(file));
        taskMonitor.monitor(task);
        exec.submit(task);
    }
    
    public File fixDefaultExtension(File file) {
        if("jpg".equalsIgnoreCase(defaultExtension)) {
            return certificateUtils.correctJPGExtension(file);
        } else {
            return certificateUtils.correctPNGExtension(file);
        }
    }
    
    public void takeImageWriteOrder(ImageWriteOrder order) throws FileNotFoundException, IOException, OutOfMemoryError {
        Debugger.log("IMAGE WRITE SERVICE : default image extension " + defaultExtension); // debug
        if(a3output) {
            if(queue == null) {
                queue = order;
                statusLabel.setText("Added to queue");
                Debugger.log("IMAGE WRITER SERVICE : added order to queue"); // debug
                // TODO status message : ADDED TO QUEUE
            } else {
                if(certificateImage == null) Debugger.log("Shit rain!"); // debug
                BufferedImage img1 = ImageUtils.createBufferedImage(certificateImage, queue.fields);
                BufferedImage img2 = ImageUtils.createBufferedImage(certificateImage, order.fields);
                Debugger.log("created two buffered images..."); // debug
                BufferedImage combined = ImageUtils.combineImages(img1, img2);
                Debugger.log("combined two images");
                /* a lil messy below */
                String combinedName = queue.saveName + order.saveName; // TODO save name for combined image
                File saveFile = new File(queue.savePath + File.separatorChar + combinedName); // assuming both are to saved in same location
                takeWork(combined, saveFile);
                queue = null;
            }
        } else {
            BufferedImage img = ImageUtils.createBufferedImage(certificateImage, order.fields);
            File saveFile = new File(order.savePath + File.separatorChar + order.saveName);
            takeWork(img, saveFile);
        }
    }
    
    
    
    
    class TaskMonitor {
        private final ReadOnlyObjectWrapper<Task> currentTask = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyStringWrapper currentTaskName = new ReadOnlyStringWrapper();
        private final ReadOnlyDoubleWrapper currentTaskProgress = new ReadOnlyDoubleWrapper();
        private final ReadOnlyBooleanWrapper idle = new ReadOnlyBooleanWrapper(true);
        private final ReadOnlyIntegerWrapper pendingTasks = new ReadOnlyIntegerWrapper();
        
        public void monitor(final Task task) {
            pendingTasks.set(pendingTasks.get() + 1); // add one to tasks, move this inside the switch
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
                            pendingTasks.set(pendingTasks.get() - 1); // reduce task count
                            idle.set(true);
                            break;
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
        
        public ReadOnlyIntegerProperty pendingTasksProperty() {
            return pendingTasks.getReadOnlyProperty();
        }
        
    }
    
}
