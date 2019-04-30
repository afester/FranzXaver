package afester.javafx.examples.fx3d;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Fx3Dsample extends Application {

    public static void main(String[] args) 
    {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) 
    {
        // Create a MeshView
        MeshView meshView = this.createMeshView();
//        meshView.setTranslateX(250);
//        meshView.setTranslateY(100);
//        meshView.setTranslateZ(400);
        
        // Scale the Meshview to make it look bigger
//        meshView.setScaleX(10.0);
//        meshView.setScaleY(10.0);
//        meshView.setScaleZ(10.0);
        
        // Create a Camera to view the 3D Shapes
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(-150);
        camera.setTranslateY(-150);
        camera.setTranslateZ(300);
        camera.setRotate(10);

        // Add a Rotation Animation to the Camera
//        RotateTransition rt = new RotateTransition(Duration.seconds(2), camera);
//        rt.setCycleCount(Animation.INDEFINITE);
//        rt.setFromAngle(-30);
//        rt.setToAngle(30);
//        rt.setAutoReverse(true);
//        rt.setAxis(Rotate.Y_AXIS);
//        rt.play();
        
        // Create the red Front Light
        PointLight redLight = new PointLight();
        redLight.setColor(Color.RED);
//        redLight.setTranslateX(250);
//        redLight.setTranslateY(150);
//        redLight.setTranslateZ(300);

        // Create the green Back Light
        PointLight greenLight = new PointLight();
        greenLight.setColor(Color.GREEN);
//        greenLight.setTranslateX(20);
//        greenLight.setTranslateY(15);
//        greenLight.setTranslateZ(45);

        
        // Add the Shapes and the Light to the Group        
        Group root = new Group(meshView, redLight, greenLight);
        // Rotate the triangle with its lights to 90 degrees
        root.setRotationAxis(Rotate.Y_AXIS);
        root.setRotate(90);
        
        // Create a Scene with depth buffer enabled
        Scene scene = new Scene(root, 400, 300, true);
        // Add the Camera to the Scene
        scene.setCamera(camera);

        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title of the Stage
        stage.setTitle("An Example using a TriangleMesh");
        // Display the Stage
        stage.show();
    }
    
    public MeshView createMeshView() 
    {
        return new Cone();
//        
//     // Vertex coordinates
//        float h = 210;    // Height (Y)
//        float w = 245;    // Width (X)
//        float d = 125;    // Depth (Z)
//
//        float[] points = {
//            0,      -h / 2,   0,        // 0
//            w / 2,  h / 2,    d / 2,    // 1
//            w / 2,  h / 2,    -d / 2,   // 2
//            -w / 2, h / 2,    -d / 2,   // 3
//            -w / 2, h / 2,    d / 2     // 4
//        };
//
////        float[] points2 = 
////        {   
////            50, 0, 0,
////            45, 10, 0,
////            55, 10, 0
////        };
//        
//        float[] texCoords = {
//                0.504f, 0.524f,     // 0
//                0.701f, 0,          // 1
//                0.126f, 0,          // 2
//                0,      0.364f,     // 3
//                0,      0.608f,     // 4
//                0.165f, 1,          // 5
//                0.606f, 1,          // 6
//                0.575f, 0.420f,     // 7
//                0.575f, 0.643f,     // 8
//                0.740f, 0.643f,     // 9
//                0.740f, 0.420f      // 10      
////        {   
////            0.5f, 0.5f,
////            0.0f, 1.0f,
////            1.0f, 1.0f
//        };
//        
//        int[] faces = {
//                0, 0, 3, 5, 2, 6, // Front face
//                0, 0, 2, 2, 1, 3, // Right face
//                0, 0, 1, 1, 4, 2, // Back face
//                0, 0, 4, 4, 3, 5, // Left right face
//                2, 9, 3, 8, 4, 7, // Bottom face
//                2, 9, 4, 7, 1, 10 // Bottom face
//        };
////        {
////            0, 0, 2, 2, 1, 1,
////            0, 0, 1, 1, 2, 2
////        };
//        
//        // Create a TriangleMesh
//        TriangleMesh mesh = new TriangleMesh();
//
//        mesh.getPoints().addAll(points);
//        mesh.getTexCoords().addAll(texCoords);
//        mesh.getFaces().addAll(faces);
//        
//        // Create a NeshView
//        MeshView meshView = new MeshView();
//        meshView.setMesh(mesh);
//        
//        return meshView;
    }
}
