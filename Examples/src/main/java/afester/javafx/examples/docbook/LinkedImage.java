package afester.javafx.examples.docbook;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fxmisc.richtext.model.Codec;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LinkedImage<S> extends CustomObject<S> {

    private String imagePath;
    private String imageFile;
    private Image image;

    public LinkedImage() {
    }

    public LinkedImage(String imagePath, String imageFile, S style) {
        super(style);
        

        // if the image is below the current working directory,
        // then store as relative path name.
//        String currentDir = System.getProperty("user.dir") + File.separatorChar;
//        if (imagePath.startsWith(currentDir)) {
//            imagePath = imagePath.substring(currentDir.length());
//        }

        this.imagePath = imagePath;
        this.imageFile = imageFile;
    }


  /**
   * @return The path where the image to render is located.
   */
  public String getImagePath() {
      return imagePath;
  }

  /**
   * @return The filename of the image to render.
   */
  public String getImageFile() {
      return imageFile;
  }

    
    @Override
    public CustomObject<S> setStyle(S style) {
        return new LinkedImage<>(imagePath, imageFile, style);
    }

    @Override
    public Node createNode() {
        if (image == null) {
            image = new Image("file:" + imagePath + "/" + imageFile);
        }

        ImageView result = new ImageView(image);
        return result;
    }

    @Override
    public void encode(DataOutputStream os) throws IOException {
      // external path rep should use forward slashes only
      String externalPath = imagePath.replace("\\", "/");
      Codec.STRING_CODEC.encode(os, externalPath);
      // styleCodec.encode(os, i.style);
    }

    @Override
    public String toString() {
        return String.format("LinkedImage[path=%s]", imagePath);
    }

    @Override
    protected void decode(DataInputStream is) throws IOException {
        // Sanitize path - make sure that forward slashes only are used
        imagePath = Codec.STRING_CODEC.decode(is);
        imagePath = imagePath.replace("\\",  "/");
        System.err.println("   " + imagePath);
//         S style = styleCodec.decode(is);
    }
}
