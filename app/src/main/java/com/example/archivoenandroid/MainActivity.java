package com.example.archivoenandroid;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    EditText cajaTexto;
    RadioGroup optGroupAlmacenmiento;
    Button btnAbrir, btnGuardar;

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
        new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                guardarArchivoMemoriaExterna();
            } else {
                Toast.makeText(this, "Lo sentimos es necesario el permiso de lo contrario queda inhabilitado el almacenamiento externo", Toast.LENGTH_LONG).show();
                btnGuardar.setEnabled(false);
            }
        }
    );

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cajaTexto = findViewById(R.id.txtTexto);
        optGroupAlmacenmiento = findViewById(R.id.optGroupTipoAlma);
        btnAbrir = findViewById(R.id.btnAbrir);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener( view -> guardarArchivo() );
        btnAbrir.setOnClickListener( view -> abrirArchivo() );

    }

    private void abrirArchivo() {
        if(optGroupAlmacenmiento.getCheckedRadioButtonId() == R.id.optExterna){
            abrirArchivoExterno();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void guardarArchivo() {
       if(optGroupAlmacenmiento.getCheckedRadioButtonId() == R.id.optExterna){
           validarPermiso();
       }
    }

    private void abrirArchivoExterno() {
        File pathExternal = getExternalFilesDir(null);
        File file = new File(pathExternal, "MiArchivo.txt");

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String texto = "";
            int car = -1;
            do {
                car = fileInputStream.read();
                if(car != -1){
                    texto += (char)car;
                }
            }while(car != -1);

            cajaTexto.setText(texto);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void validarPermiso(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            guardarArchivoMemoriaExterna();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Es necesario para escribir el archivo en la memoria externa", Toast.LENGTH_LONG).show();
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void guardarArchivoMemoriaExterna() {
        File pathExternal = getExternalFilesDir(null);
        File file = new File(pathExternal, "MiArchivo.txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(cajaTexto.getText().toString().getBytes());
            fileOutputStream.close();
            cajaTexto.setText("");
            for (String nombre : getExternalFilesDir(null).list()){
                Log.d("ARCHIVO", nombre);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}