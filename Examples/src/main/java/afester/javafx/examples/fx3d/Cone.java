package afester.javafx.examples.fx3d;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class Cone extends MeshView {

    int rounds = 360;
    int r1 = 100;
    int r2 = 50;
    int h = 100;
    
    public Cone() {
        Group cone = new Group();
        PhongMaterial material = new PhongMaterial(Color.BLUE);
    
        float[] points = new float[rounds *12];
        float[] textCoords = {
                0.5f, 0,
                0, 1,
                1, 1
        };
        int[] faces = new int[rounds *12];
    
        for(int i= 0; i<rounds; i++){
            int index = i*12;
            //0
            points[index] = (float)Math.cos(Math.toRadians(i))*r2;
            points[index+1] = (float)Math.sin(Math.toRadians(i))*r2;
            points[index+2] = h/2;
            //1
            points[index+3] = (float)Math.cos(Math.toRadians(i))*r1;
            points[index+4] = (float)Math.sin(Math.toRadians(i))*r1;
            points[index+5] = -h/2;
            //2
            points[index+6] = (float)Math.cos(Math.toRadians(i+1))*r1;
            points[index+7] = (float)Math.sin(Math.toRadians(i+1))*r1;
            points[index+8] = -h/2;
            //3
            points[index+9] = (float)Math.cos(Math.toRadians(i+1))*r2;
            points[index+10] = (float)Math.sin(Math.toRadians(i+1))*r2;
            points[index+11] = h/2;        
        }
    
        for(int i = 0; i<rounds ; i++){
            int index = i*12;
            faces[index]=i*4;
            faces[index+1]=0;
            faces[index+2]=i*4+1;
            faces[index+3]=1;
            faces[index+4]=i*4+2;
            faces[index+5]=2;
    
            faces[index+6]=i*4;
            faces[index+7]=0;
            faces[index+8]=i*4+2;
            faces[index+9]=1;
            faces[index+10]=i*4+3;
            faces[index+11]=2;
        }
    
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(textCoords);
        mesh.getFaces().addAll(faces);
    
//        Cylinder circle1 = new Cylinder(r1, 0.1);
//        circle1.setMaterial(material);
//        circle1.setTranslateZ( -h / 2);
//        circle1.setRotationAxis(Rotate.X_AXIS);
//        circle1.setRotate(90);
//    
//        Cylinder circle2 = new Cylinder(r2, 0.1);
//        circle2.setMaterial(material);
//        circle2.setTranslateZ( h / 2);
//        circle2.setRotationAxis(Rotate.X_AXIS);
//        circle2.setRotate(90);
//    
//    
         // MeshView meshView = new MeshView();
        /*meshView.*/setMesh(mesh);
        /*meshView.*/setMaterial(material);
          setDrawMode(DrawMode.FILL); //  .LINE);

//        cone.getChildren().addAll(meshView);
//        Rotate r1 = new Rotate(90, Rotate.X_AXIS);
//        cone.getTransforms().add(r1);
//        getChildren().addAll(cone);
    }
}
