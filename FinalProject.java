import javafx.application.Application;
import javafx.application.Platform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
//the real suff
public class FinalProject extends Application
{
    @Override
    public void init()
    {
    	
    }
    @Override
    public void start(final Stage primaryStage)
    {
    	
    }
    @Override
    public void stop()
    {
    	
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}
public class JavaPicIOTest
{
	public JavaPicIOTest()
	{
		try
		{
      	BufferedImage pic = ImageIO.read(new File("/Users/al/some-picture.jpg"));
      	//do stuff witht he pic here
		} 
    	catch (IOException e)
    	{
    	}
	}
}
