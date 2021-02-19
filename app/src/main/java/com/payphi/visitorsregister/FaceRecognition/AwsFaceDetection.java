package com.payphi.visitorsregister.FaceRecognition;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.AgeRange;
import com.amazonaws.services.rekognition.model.Attribute;
import com.amazonaws.services.rekognition.model.DetectFacesRequest;
import com.amazonaws.services.rekognition.model.DetectFacesResult;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.RegisterFragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

public class AwsFaceDetection extends AppCompatActivity {
    AmazonRekognition rekognitionClient = new AmazonRekognitionClient(new BasicAWSCredentials("AKIAIAXZ2GHXM2VJ4NPA", "U5wx5VqPceZWaEBIb3elieAVVfkZz7RnXlMzc8XH"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aws_face_detection);
        Test();
    }

    private void Test() {


    }
    public void detectFace(ByteArrayInputStream inputStream){
        new RetrieveFeedTask().execute(inputStream);
    }
    class RetrieveFeedTask extends AsyncTask<InputStream,Void,Void > {

        private Exception exception;
        @Override
        public Void doInBackground(InputStream...params) {
            String face_description="";
            try{
            Image source = new Image()
                    .withBytes(ByteBuffer.wrap(IOUtils.toByteArray(params[0])));
            DetectFacesRequest request = new DetectFacesRequest()
                    .withImage(source)
                    .withAttributes(String.valueOf(Attribute.ALL));

            try {
                DetectFacesResult result = rekognitionClient.detectFaces(request);
                List< FaceDetail > faceDetails = result.getFaceDetails();

                for (FaceDetail face: faceDetails) {
                    if (request.getAttributes().contains("ALL")) {
                        AgeRange ageRange = face.getAgeRange();
                     boolean flag =    face.getEyeglasses().isValue();
                       // Toast.makeText(getApplicationContext(),flag,Toast.LENGTH_LONG).show();

                        System.out.println("The detected face is estimated to be between "
                                + ageRange.getLow().toString() + " and " + ageRange.getHigh().toString()
                                + " years old.");
                        System.out.println("Here's the complete set of attributes:"+face.toString());
                    } else { // non-default attributes have null values.
                        System.out.println("Here's the default set of attributes:");
                    }
                    HashMap<String,String> map = new HashMap();
                    map.put("Gender",face.getGender().getValue() );
                    map.put("Mustache",face.getMustache().getValue().toString());
                    map.put("Beard",face.getBeard().getValue().toString());
                    map.put("Eyeglass",face.getEyeglasses().isValue().toString());
                    map.put("Age",face.getAgeRange().toString());
                    //face_description = "Gender : "+face.getGender().getValue() +  "," + face.getMustache().getValue() + "," + face.getBeard().getValue() + ","  + face.getEyeglasses().isValue() + "," + face.getAgeRange();
                    RegisterFragment.getInstance().setFacialData(map);
                  //  ObjectMapper objectMapper = new ObjectMapper();
                   // System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            }catch (Exception e){

            }
        return null;
        }

        protected void onPostExecute() {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }
}
