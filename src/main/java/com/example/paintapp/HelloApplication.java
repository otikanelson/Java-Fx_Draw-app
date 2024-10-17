package com.example.paintapp;

package com.example.paintapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Objects;

public class PaintApplication extends Application {
    private Color currentColor = Color.BLACK;
    private double startX, startY;
    private ImageView cursorImageView;
    private Canvas canvas;

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setOnAction(event -> {
            currentColor = colorPicker.getValue();
            setCursorImage("paintbrush.png");
        });

        Button rectangleButton = new Button("Rectangle");
        rectangleButton.setOnAction(event -> {
            setCursorImage("rectangle.png");
            setRectangleTool(gc);
        });

        Button circleButton = new Button("Circle");
        circleButton.setOnAction(event -> {
            setCursorImage("circle.png");
            setCircleTool(gc);
        });

        Button lineButton = new Button("Line");
        lineButton.setOnAction(event -> {
            setCursorImage("line.png");
            setLineTool(gc);
        });

        Button pencilButton = new Button("Pencil");
        pencilButton.setOnAction(event -> {
            setCursorImage("pencil.png");
            setPencilTool(gc);
        });

        TextField textField = new TextField();
        Button textButton = new Button("Add Text");
        textButton.setOnAction(event -> {
            setCursorImage("text.png");
            addText(gc, textField.getText());
        });

        Button eraserButton = new Button("Eraser");
        eraserButton.setOnAction(event -> {
            setCursorImage("eraser.png");
            setEraserTool(gc);
        });

        Button clearButton = new Button("Clear All");
        clearButton.setOnAction(event -> {
            clearCanvas(gc);
        });

        ToolBar toolBar = new ToolBar(colorPicker, rectangleButton, circleButton, lineButton, pencilButton, textField, textButton, eraserButton, clearButton);

        cursorImageView = new ImageView();
        cursorImageView.setMouseTransparent(true);

        BorderPane root = new BorderPane();
        root.setTop(toolBar);
        root.setCenter(canvas);
        root.getChildren().add(cursorImageView);

        Scene scene = new Scene(root, 800, 600);
        scene.setCursor(javafx.scene.Cursor.NONE);

        // Add event handlers for all mouse events
        canvas.setOnMouseMoved(this::updateCursorPosition);
        canvas.setOnMouseDragged(this::updateCursorPosition);
        canvas.setOnMousePressed(this::updateCursorPosition);
        canvas.setOnMouseReleased(this::updateCursorPosition);

        scene.setOnMouseExited(event -> cursorImageView.setVisible(false));
        scene.setOnMouseEntered(event -> cursorImageView.setVisible(true));

        primaryStage.setTitle("Paint Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Set initial cursor
        setCursorImage("paintbrush.png");
    }

    private void setCursorImage(String imagePath) {
        try {
            Image cursorImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + imagePath)));
            cursorImageView.setImage(cursorImage);
            cursorImageView.setFitWidth(32);
            cursorImageView.setFitHeight(32);
        } catch (NullPointerException e) {
            System.out.println("Error: Image not found: " + imagePath);
        }
    }

    private void updateCursorPosition(MouseEvent event) {
        cursorImageView.setLayoutX(event.getX() + canvas.getLayoutX() - cursorImageView.getFitWidth() / 2);
        cursorImageView.setLayoutY(event.getY() + canvas.getLayoutY() - cursorImageView.getFitHeight() / 2);
    }

    private void setRectangleTool(GraphicsContext gc) {
        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
        });

        canvas.setOnMouseReleased(event -> {
            double endX = event.getX();
            double endY = event.getY();
            gc.setFill(currentColor);
            gc.fillRect(Math.min(startX, endX), Math.min(startY, endY),
                    Math.abs(endX - startX), Math.abs(endY - startY));
        });
    }

    private void setCircleTool(GraphicsContext gc) {
        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
        });

        canvas.setOnMouseReleased(event -> {
            double endX = event.getX();
            double endY = event.getY();
            double radius = Math.hypot(endX - startX, endY - startY);
            gc.setFill(currentColor);
            gc.fillOval(startX - radius, startY - radius, radius * 2, radius * 2);
        });
    }

    private void setLineTool(GraphicsContext gc) {
        canvas.setOnMousePressed(event -> {
            startX = event.getX();
            startY = event.getY();
        });

        canvas.setOnMouseReleased(event -> {
            double endX = event.getX();
            double endY = event.getY();
            gc.setStroke(currentColor);
            gc.strokeLine(startX, startY, endX, endY);
        });
    }

    private void setPencilTool(GraphicsContext gc) {
        canvas.setOnMousePressed(event -> {
            gc.beginPath();
            gc.moveTo(event.getX(), event.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged(event -> {
            gc.lineTo(event.getX(), event.getY());
            gc.setStroke(currentColor);
            gc.stroke();
        });
    }

    private void setEraserTool(GraphicsContext gc) {
        canvas.setOnMousePressed(event -> {
            double x = event.getX();
            double y = event.getY();
            gc.clearRect(x - 5, y - 5, 10, 10);
        });

        canvas.setOnMouseDragged(event -> {
            double x = event.getX();
            double y = event.getY();
            gc.clearRect(x - 5, y - 5, 10, 10);
        });
    }

    private void addText(GraphicsContext gc, String text) {
        canvas.setOnMouseClicked(event -> {
            gc.setFill(currentColor);
            gc.fillText(text, event.getX(), event.getY());
        });
    }

    private void clearCanvas(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
}


