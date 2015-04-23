/*
 * Copyright (c) 2012-2015 BCZ Inc.
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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author bazi
 */
public final class CertificateAvatar implements CertificateNode {
    
    private final ImageView imageView;
    private CertificateField observer;
    private CertificateTab container;

    public CertificateAvatar(Image image) {
        imageView = new ImageView(image);
    }

    @Override
    public CertificateTab getContainer() {
        return container;
    }
    
    @Override
    public void setContainer(CertificateTab ct) {
        this.container = ct;
    }

    @Override
    public CertificateField getObserver() {
        return observer;
    }

    @Override
    public void setObserver(CertificateField observer) {
        this.observer = observer;
    }
    
    public void setImage(Image image) {
        imageView.setImage(image);
    }
    
    
    @Override
    public void setX(int x) {
        imageView.setX(x);
    }
    
    @Override
    public int getX() {
        return (int) imageView.getX();
    }
    
    @Override
    public void setY(int y) {
        imageView.setY(y);
    }
    
    @Override
    public int getY() {
        return (int) imageView.getY();
    }
    
    public int getWidth() {
        return (int) imageView.getImage().getWidth();
    }
    
    public int getHeight() {
        return (int) imageView.getImage().getHeight();
    }
    
    public ReadOnlyObjectProperty<Bounds> layoutBoundsProperty() {
        return imageView.layoutBoundsProperty();
    }
    
    public ReadOnlyObjectProperty<Image> imageProperty() {
        return imageView.imageProperty();
    }

    public void setOnMousePressed(EventHandler<MouseEvent> mouseHandler) {
        imageView.setOnMousePressed(mouseHandler);
    }

    public void setOnMouseDragged(EventHandler<MouseEvent> mouseHandler) {
        imageView.setOnMouseDragged(mouseHandler);
    }

    public void setOnMouseReleased(EventHandler<MouseEvent> mouseHandler) {
        imageView.setOnMouseReleased(mouseHandler);
    }
    
    @Override
    public ImageView get() {
        return imageView;
    }
    
    @Override
    public FieldType getFieldType() {
        return FieldType.IMAGE;
    }

    
    @Override
    public void setAttributes(CertificateField changes) {
        Image image = container.createImage(changes.getWidth(), changes.getHeight());
        setImage(image);
    }

    @Override
    public CertificateField getAttributes() {
        return new CertificateField(getX(), getY(), FieldType.IMAGE, getWidth(), getHeight());
    }
    
}
