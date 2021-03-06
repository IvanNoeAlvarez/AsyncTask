package com.example.ivalion.asynctask;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btnInicio;
    Button btnCancelar;
    TextView msnIf;
    TextView porcentaje;
    ProgressBar progress;
    int inicioProgreso = 0;
    final int progresoMax = 100;
    CheckBox ch;

    LinearLayout verde,azul;

    FloatingActionButton fabCreate;


    AsyncTask1 async = new AsyncTask1();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msnIf = (TextView) findViewById(R.id.msnIf);
        btnInicio = (Button) findViewById(R.id.btnInicio);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);


        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Descargando...", Toast.LENGTH_SHORT).show();

                //Al pulsar iniciar empezara el hilo y desaparecera el boton
                async.execute();
                btnInicio.setVisibility(View.INVISIBLE);

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Descarga cancelada", Toast.LENGTH_SHORT).show();

                //Al pulsar se cancelara el hilo
                async.cancel(true);
            }
        });


        /////////////////////////////
        ch = (CheckBox) findViewById(R.id.ch);
        fabCreate = (FloatingActionButton) findViewById(R.id.floating);


        registerForContextMenu(fabCreate);

        final View.OnTouchListener mover = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                float dX = 0;
                float dY = 0;
                float lastAction = 0;
                Intent intent;
                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        dX = event.getRawX() - view.getX();
                        dY = event.getRawY() - view.getY();

                        lastAction = MotionEvent.ACTION_DOWN;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        lastAction = MotionEvent.ACTION_MOVE;
                        view.setY(event.getRawY() + dY - 149.0f);
                        view.setX(event.getRawX() + dX);
                        break;
                    case MotionEvent.ACTION_UP:
                        lastAction = MotionEvent.ACTION_UP;
                        if (ch.isChecked() == false)
                            openContextMenu(view);
                        break;

                    case MotionEvent.ACTION_BUTTON_PRESS:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        return false;
                }

                return true;
            }
        };

        fabCreate.setOnTouchListener(mover);



    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        if (v.getId() == R.id.floating) {
            inflater.inflate(R.menu.context_punto, menu);
        }

    }

    private class AsyncTask1 extends AsyncTask<String, Float, Integer> {

        // Preparar componentes, mensajes para posteriormente ejecutarlo
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            msnIf.setText("Hello World!");
            // se instancia la barra de progreso y el TextView aqui, ya que son los widgets que solo se ejecutan en el hilo
            progress = (ProgressBar) findViewById(R.id.progress_bar);
            porcentaje = (TextView) findViewById(R.id.porcentajeT);
        }

        // Función donde tendrá las operaciones que se ejecutan en 2Âº plano
        @Override
        protected Integer doInBackground(String... strings) {
            //Mientras inicio=0 sea menor que progresoMax=100 se estara ejecutando
            while (inicioProgreso < progresoMax) {
                try {
                    Thread.sleep(1000);
                    // definir como ira aumentando la barra de progreso de inicio a max
                    inicioProgreso += 5;

                    //Enviar el progreso a la clase principal
                    publishProgress(Float.valueOf(inicioProgreso));

                } catch (InterruptedException e) {
                    e.getMessage();
                    cancel(true);
                }


            }

            return null;
        }

        // Función que actualizará los componentes o la vista de progreso del hilo
        @Override
        protected void onProgressUpdate(Float... porcentajeP) {
            super.onProgressUpdate(porcentajeP);
            msnIf.setText("Se estan descargando");

            msnIf.setTextColor(Color.BLACK);
            // la barra de progreso se ira incrementando según se ha marcado en doInBackground
            progress.setProgress(Math.round(porcentajeP[0]));
            porcentaje.setText("" + Math.round(porcentajeP[0]) + " %");

        }

        // Función que se ejecutará al acabar el ProgressUpdate
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            msnIf.setText("Se han acabado las descargas");
            msnIf.setTextColor(Color.GREEN);
        }


        // Función que se lanzará si el usuario decidiera cancelar la ejecución del hilo.
        @Override
        protected void onCancelled() {
            super.onCancelled();
            msnIf.setText("Has cancelado las descargas");
            msnIf.setTextColor(Color.RED);
        }
    }

}
