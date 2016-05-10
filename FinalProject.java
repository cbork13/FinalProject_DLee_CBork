import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.BorderPane;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TabPane;
import javafx.scene.control.Button;
import javafx.scene.control.Slider; 
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.scene.SnapshotParameters;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.geometry.Insets;
import javafx.event.EventHandler;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class FinalProject2 extends Application
{
    private GraphicsContext brush;
    private double brushSize;
    private BorderPane bp;
    private int shape;
    private MenuBar mbar; 
    private BorderPane bpOuter; 
    private TabPane tp; 
    private GridPane gp; 
    private Canvas canvas; 
    private Canvas miniCanvas; 
    private TextField textField; 
    private Color currentColor;
    private Color backgroundColor;
    private WritableImage wim; 
    private Stage primaryStage; 
    final static int CANVAS_WIDTH = 800;
    final static int CANVAS_HEIGHT = 500;
    private MenuItem saveAsItem;
    private Stage primary;
    private File file;
    Slider r; 
    Slider b; 
    Slider g;
    Slider size; 
    
    @Override
    public void init()
    {
        currentColor = Color.BLACK;
        backgroundColor = Color.WHITE;
        r = new Slider(0,255,0);
        g = new Slider(0,255,0);
        b = new Slider(0,255,0);
        size = new Slider(0, 25, 1);
        mbar = new MenuBar();
        miniCanvas = new Canvas(80,40); 
        saveAsItem = new MenuItem("Save As...");
        bpOuter = new BorderPane(); 
        bp = new BorderPane();
        gp = new GridPane();
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        backgroundColor = Color.WHITE;
        brushSize = new Double(1);
        //designators for the chape choice pen = 0, circle = 1, triangle = 2, rect = 3
        shape = 0;
        WritableImage wim = new WritableImage(800,500); 
    }
    //
    @Override
    public void start(final Stage primaryStage) {
        //begins to set the items into their places
        this.primary = primaryStage;
        brush = canvas.getGraphicsContext2D();
        sliderFormat(r);
        sliderFormat(g);
        sliderFormat(b);
        sliderFormat(size);
        primaryStage.setTitle("Paint");
        bpOuter.setTop(mbar);
        bpOuter.setCenter(bp);
        bp.setTop(gp);
        bp.setCenter(canvas); 
        createFileMenu();
        Scene scene = new Scene(bpOuter,830,700);
        primaryStage.setScene(scene);
        primaryStage.show();  

        //Takes in the input in the textfield and updates the sliders
        textField.setOnAction( e -> {
            String s = textField.getText();
            if(s.startsWith("0x"))
                   {
                s = s.substring(2);//Strips off 0x 
            }
            try
            {
            int h = Integer.parseInt(s,16);//parsing it in hex
            currentColor = Color.web(s);
            refreshC();
            SliderUpdate();//Put it in the right listener 
            }
            catch(NumberFormatException ex)
            {
                textField.setText("Error"); //if the hexcode input is invalid 
            }
        });        
                //The slider listeners for both refresing the color and the hexcode. 
        r.valueProperty().addListener( (ov, old_val, new_val) -> {
            currentColor = Color.rgb((int)r.getValue(), (int) g.getValue(), (int) b.getValue());
            //currentColor = Color.rgb((int)red.getValue(), (int) (255* currentColor.getGreen()), (int)(255* currentColor.getBlue()));
            refreshC();
            hexCode();
        });

        g.valueProperty().addListener( (ov, old_val, new_val) -> {
            currentColor = Color.rgb((int)r.getValue(), (int) g.getValue(), (int) b.getValue()); //update only green 
            //currentColor = Color.rgb((int)(255*currentColor.getRed()), (int)green.getValue(), (int)(255*currentColor.getBlue())); 
            refreshC();
            hexCode();
        }); 
        b.valueProperty().addListener( (ov, old_val, new_val) -> {
            currentColor = Color.rgb((int)r.getValue(), (int) g.getValue(), (int) b.getValue());
            //currentColor = Color.rgb((int)(255*currentColor.getRed()), (int)(255*currentColor.getGreen()), (int)blue.getValue());
            refreshC();
            hexCode();
        });
        size.valueProperty().addListener( (ov, old_val, new_val) -> {
            brushSize = (double) new_val;
            brush.setLineWidth(brushSize);
        });
        //sets up for the pen strokes
        brush.setStroke(currentColor);
        brush.setFill(Color.WHITE);
        brush.setStroke(Color.BLACK);
        brush.setLineWidth(brushSize);
        //starts the drawing function
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, 
                new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                brush.beginPath();
                brush.moveTo(event.getX(), event.getY());
                //for use in the triangle pen shape
                double[] cornerX = new double[3];
                cornerX[0] = event.getX()+1;  //filling one element at a time
                cornerX[1] = event.getX()-1;
                cornerX[2] = event.getX();
                double[] cornerY = new double[3];
                cornerY[0] = event.getY();  //filling one element at a time
                cornerY[1] = event.getY();
                cornerY[2] = event.getY()+1;
                //didstinguishes between pen styles
                if(shape == 0)
                {
                    brush.lineTo(event.getX(), event.getY());
                    brush.stroke();
                } else if(shape == 1){
                    brush.strokeOval(event.getX(), event.getY(), 1, 1);
                } else if(shape == 2){
                    brush.strokePolygon(cornerX,cornerY, 3);
                } else {
                    brush.strokeRect(event.getX(), event.getY(), 1, 1);
                }
            }
        });
        // similar to above but contiues to follow the mouse as teh pen is being used
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, 
                new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                
                double[] cornerX = new double[3];
                cornerX[0] = event.getX()+1;  //filling one element at a time
                cornerX[1] = event.getX()-1;
                cornerX[2] = event.getX();
                double[] cornerY = new double[3];
                cornerY[0] = event.getY();  //filling one element at a time
                cornerY[1] = event.getY();
                cornerY[2] = event.getY()+1;
                if(shape == 0)
                {
                    brush.lineTo(event.getX(), event.getY());
                    brush.stroke();
                } else if(shape == 1){
                    brush.strokeOval(event.getX(), event.getY(), 1, 1);
                } else if(shape == 2){
                    brush.strokePolygon(cornerX,cornerY, 3);
                } else {
                    brush.strokeRect(event.getX(), event.getY(), 1, 1);
                }
            }
        });
        // ends the drawing function 
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, //ends the drawing stroke
                new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event)
            {
            }
        });   //ends drawing method
    saveAsItem.setOnAction( e -> {
            FileChooser fileChooser = new FileChooser(); 
            //Set extension filter
            FileChooser.ExtensionFilter extFilter = 
                    new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            //Show save file dialog
            file = fileChooser.showSaveDialog(primaryStage);
            if(file != null){
                try {
                    WritableImage writableImage = new WritableImage(CANVAS_WIDTH, CANVAS_HEIGHT);
                    canvas.snapshot(null, writableImage);
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    Logger.getLogger(FinalProject.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    //Stop Function to add to if a failsafe feature was to be added
    @Override
    public void stop()
    {
        System.out.println("happy painting!");
    }
    public static void main(String[] args)
    {
        launch(args);
    }
    private void refreshC()
    {
       //This refreshes the canvas's current color. 
       GraphicsContext g = miniCanvas.getGraphicsContext2D();
       g.setFill(currentColor);
       g.fillRect(0,0,miniCanvas.getWidth(),miniCanvas.getHeight());
       brush.setStroke(currentColor);
       brush.setLineWidth(brushSize);

    }
    private void hexCode()
    {
        //Sets the text in the textfield based on the sliders and the proper hexcode format
        textField.setText(String.format("0x%02X%02X%02X",(int)r.getValue(),(int)g.getValue(),(int)b.getValue()));
    }
    private void SliderUpdate()
    {
        //Based on the position of the slider, it sets it equal to a double variable (r,g, or b). 
        double rr = 255*currentColor.getRed();
        double gg = 255*currentColor.getGreen();
        double bb = 255*currentColor.getBlue();
        
        //Setting the value of the sliders to correspond to the colors in the canvas (basically the doubles above).  
        r.setValue(rr);
        g.setValue(gg);
        b.setValue(bb);

        refreshC();
    }
    //creates the menu items and sets thier functions
    //does not include save becasue save would only wokr in the start method
    private void createFileMenu()
    {
        Menu fileMenu = new Menu("File");
        Menu shapeMenu = new Menu("Pen Style");
        Menu backgroundMenu = new Menu("Background");
        Menu penMenu = new Menu("Pen");
        
        mbar.getMenus().addAll(fileMenu, shapeMenu, backgroundMenu, penMenu);
        //fileMenu
        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setOnAction( e -> {
            Platform.exit();
        });
        //designators for the chape choice pen = 0, circle = 1, triangle = 2, rect = 3
        MenuItem pen = new MenuItem("Pen");
        pen.setOnAction( e -> {
            shape = 0;
            brush.setStroke(currentColor);
            brush.setLineWidth(brushSize);
        });
        MenuItem square = new MenuItem("Square");
        square.setOnAction( e -> {
            shape = 3;
            brush.setStroke(currentColor);
            brush.setLineWidth(brushSize);
        });
        MenuItem circle = new MenuItem("Circle");
        circle.setOnAction( e -> {
            shape = 1;
            brush.setStroke(currentColor);
            brush.setLineWidth(brushSize);
        });
        MenuItem triangle = new MenuItem("Triangle");
        triangle.setOnAction( e -> {
            shape = 2;
            brush.setStroke(currentColor);
            brush.setLineWidth(brushSize);
        }); 
        //backgroundMenu
        BGItem redBGItem = new BGItem(Color.RED,"red");
        BGItem greenBGItem = new BGItem(Color.GREEN,"green");
        BGItem blueBGItem = new BGItem(Color.BLACK,"black");
        BGItem whiteBGItem = new BGItem(Color.WHITE,"white");
        //penMenu
        MenuItem eraserItem = new MenuItem("Erase");
        eraserItem.setOnAction( e -> {
            brush.setStroke(backgroundColor);
            brush.setLineWidth(25);
        });
        
        //stick 'em in
        fileMenu.getItems().addAll(saveAsItem, quitItem);
        shapeMenu.getItems().addAll(pen, square, circle, triangle);
        backgroundMenu.getItems().addAll(redBGItem, greenBGItem, blueBGItem, whiteBGItem);
        penMenu.getItems().addAll(eraserItem);

        //labels
        Label label1 = new Label("red");
        label1.setTextFill(Paint.valueOf(Color.RED.toString()));
        Label label2 = new Label("green");
        label2.setTextFill(Paint.valueOf(Color.GREEN.toString()));
        Label label3 = new Label("blue");
        label3.setTextFill(Paint.valueOf(Color.BLUE.toString()));
        Label label4 = new Label("hexcode");
        textField = new TextField ();
        Label label5 = new Label("current color");
        Label label6 = new Label("Paintbrush Size");

        //Setting positions
        gp.setHgap(30);
        gp.setVgap(15);
        gp.setPadding(new Insets(12, 12, 12, 12));
        gp.add(label1, 0,0);
        gp.add(label2,1,0);
        gp.add(label3,2,0);
        gp.add(label4,3,0);
        gp.add(label5,4,0);
        gp.add(label6,5,0);
        gp.add(r,0,1);
        gp.add(g,1,1);
        gp.add(b,2,1);
        gp.add(size,5,1);
        gp.add(textField,3,1);
        gp.add(miniCanvas,4,1);
        gp.setHalignment(label1, HPos.CENTER);
        gp.setHalignment(label2, HPos.CENTER);
        gp.setHalignment(label3, HPos.CENTER);
        gp.setHalignment(label4, HPos.CENTER);
        gp.setHalignment(label5, HPos.CENTER);
        gp.setHalignment(label6, HPos.CENTER);
        gp.setHalignment(r, HPos.CENTER);
        gp.setHalignment(g, HPos.CENTER);
        gp.setHalignment(b, HPos.CENTER);
        gp.setHalignment(size, HPos.CENTER);
        gp.setHalignment(textField, HPos.CENTER);
        gp.setHalignment(miniCanvas,HPos.CENTER);
    }
    //correctly formats sliders so they look good
    private void sliderFormat(Slider item)
    {
        item.setMajorTickUnit(42);
        item.setShowTickLabels(true);
        item.setShowTickMarks(true);
    }
    //refreshes only the canvas
    private void refresh()
    {
       GraphicsContext h = canvas.getGraphicsContext2D();
       h.setFill(backgroundColor);
       h.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
    }
    //to minimize duplicate code when changing color in the background color feature.
    class BGItem extends MenuItem
    {
        public BGItem(Color c, String colorName)
        {
            super(colorName);
            setOnAction( e -> 
                        {
                backgroundColor = c;
                refresh();//should change right away 
            });
        }
    }
}
