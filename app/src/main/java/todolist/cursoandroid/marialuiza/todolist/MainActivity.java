package todolist.cursoandroid.marialuiza.todolist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText textoTarefa;
    private Button botaoAdicionar;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDados;
    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            //Recuperar componentes
            textoTarefa = (EditText) findViewById(R.id.textoId);
            botaoAdicionar = (Button) findViewById(R.id.botaoId);

            //Lista
            listaTarefas = (ListView) findViewById(R.id.listViewId);

            //Banco de dados
            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);

            //Tabela tarefas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String textoDigitado = textoTarefa.getText().toString();
                    salvarTarefa(textoDigitado);

                }
            });

            listaTarefas.setLongClickable(true);
            listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removerTarefa(ids.get(position));
                    return true;
                }
            });

            //Recuperar tarefas
            recuperarTarefas();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void salvarTarefa(String texto){

        try{
            if (!texto.equals("")){
                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES('" + texto + "')");
                recuperarTarefas();
                textoTarefa.setText("");
                Toast.makeText(MainActivity.this, "Tarefa salva com sucesso", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "Digite uma tarefa", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void recuperarTarefas(){
        try{
            //Recuperar tarefas
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            //Recuperar os dados das colunas
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //Criar adaptador
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                    itens);
            listaTarefas.setAdapter(itensAdaptador);

            //Listar tarefas
            cursor.moveToFirst();
            while (cursor != null){
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));

                cursor.moveToNext();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removerTarefa(Integer id){
        try {
            bancoDados.execSQL("DELETE FROM tarefas WHERE id=" + id);
            recuperarTarefas();
            Toast.makeText(MainActivity.this, "Tarefa removida com sucesso", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
