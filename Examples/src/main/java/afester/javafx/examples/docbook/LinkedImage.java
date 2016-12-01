package afester.javafx.examples.docbook;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LinkedImage<S> extends CustomObject<S> {

    private String imagePath;

    public LinkedImage(String imagePath, S style) {
        super(style);
        

        // if the image is below the current working directory,
        // then store as relative path name.
        String currentDir = System.getProperty("user.dir") + File.separatorChar;
        if (imagePath.startsWith(currentDir)) {
            imagePath = imagePath.substring(currentDir.length());
        }

        this.imagePath = imagePath;
    }


  /**
   * @return The path of the image to render.
   */
  public String getImagePath() {
      return imagePath;
  }

    
    @Override
    public CustomObject<S> setStyle(S style) {
        return new LinkedImage<>(imagePath, style);
    }

    @Override
    public Node createNode() {
        Image image = new Image("file:" + imagePath); // XXX: No need to create new Image objects each time -
                                                      // could be cached in the model layer
        ImageView result = new ImageView(image);
        return result;
    }

    @Override
    public void encode(DataOutputStream os) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String toString() {
        return String.format("LinkedImage[path=%s]", imagePath);
    }
}
