package dam.pmdm.spyrothedragon;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import android.view.WindowInsetsController;


public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);
        getWindow().setDecorFitsSystemWindows(false);

        // Configurar el VideoView
        videoView = findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.spyrovideoa;
        Uri uri = Uri.parse(videoPath);

        videoView.setVideoURI(uri);
        videoView.start();

        // Cerrar actividad al finalizar el video
        videoView.setOnCompletionListener(mp -> finish());

        // Hacer que el video se reproduzca en pantalla completa con WindowInsetsController
        View decorView = getWindow().getDecorView();
        WindowInsetsController controller = decorView.getWindowInsetsController();

    }
}


