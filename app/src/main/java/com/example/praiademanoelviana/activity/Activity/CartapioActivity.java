package com.example.praiademanoelviana.activity.Activity;import androidx.appcompat.app.AppCompatActivity;import androidx.appcompat.widget.Toolbar;import androidx.recyclerview.widget.LinearLayoutManager;import androidx.recyclerview.widget.RecyclerView;import android.app.AlertDialog;import android.content.DialogInterface;import android.os.Bundle;import android.view.Menu;import android.view.MenuInflater;import android.view.MenuItem;import android.view.View;import android.widget.AdapterView;import android.widget.EditText;import android.widget.ImageView;import android.widget.TextView;import android.widget.Toast;import com.example.praiademanoelviana.R;import com.example.praiademanoelviana.activity.Adpter.AdapterProduto;import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;import com.example.praiademanoelviana.activity.Helper.UsuarioFirebase;import com.example.praiademanoelviana.activity.lister.RecyclerItemClickListener;import com.example.praiademanoelviana.activity.model.Empresa;import com.example.praiademanoelviana.activity.model.ItemPedido;import com.example.praiademanoelviana.activity.model.Pedido;import com.example.praiademanoelviana.activity.model.Produto;import com.example.praiademanoelviana.activity.model.Usuario;import com.google.firebase.database.DataSnapshot;import com.google.firebase.database.DatabaseError;import com.google.firebase.database.DatabaseReference;import com.google.firebase.database.ValueEventListener;import com.squareup.picasso.Picasso;import java.text.DecimalFormat;import java.util.ArrayList;import java.util.List;import dmax.dialog.SpotsDialog;public class CartapioActivity extends AppCompatActivity {private RecyclerView recyclerProdutosCardapio;private ImageView imageEmpresaCardapio;private TextView textNomeEmpresaCardapio;private Empresa empresaSelecionada;private TextView textCarrinhoQuantidade;private TextView textCarrinhoTotal;private AlertDialog dialog;    private AdapterProduto adapterProduto;    private List<Produto> produtos = new ArrayList<>();    private List<ItemPedido> itensCarrinho = new ArrayList<>();    private DatabaseReference firebaseRef;    private String idEmpresa;    private String idUsuarioLogado;    private Usuario usuario;    private Pedido pedidoRecuperado;    private  int qtItensCarrinho;    private Double totalCarrinho;    private  int metodoPagamento;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_cartapio);        //Configurações iniciais        inicializarComponentes();        firebaseRef = ConfiruFirebase.getFirebase();        idUsuarioLogado = UsuarioFirebase.getIdUsuario();        //Recuperar empresa selecionada        Bundle bundle = getIntent().getExtras();        if( bundle != null ){            empresaSelecionada = (Empresa) bundle.getSerializable("empresas");            textNomeEmpresaCardapio.setText( empresaSelecionada.getNome() );            idEmpresa = empresaSelecionada.getIdUsuario();            String url = empresaSelecionada.getUrlImagem();            Picasso.get().load(url).into(imageEmpresaCardapio);        }        //Configurações Toolbar        Toolbar toolbar = findViewById(R.id.toolbarNova);        toolbar.setTitle("Cardápio");        setSupportActionBar(toolbar);        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        //Configura recyclerview        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));        recyclerProdutosCardapio.setHasFixedSize(true);        adapterProduto = new AdapterProduto(produtos, this);        recyclerProdutosCardapio.setAdapter( adapterProduto );        //Configurar evento de clique        recyclerProdutosCardapio.addOnItemTouchListener(                new RecyclerItemClickListener(                        this,                        recyclerProdutosCardapio,                        new RecyclerItemClickListener.OnItemClickListener() {                            @Override                            public void onItemClick(View view, int position) {                                confirmarQuantidade(position);                            }                            @Override                            public void onLongItemClick(View view, int position) {                            }                            @Override                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                            }                        }                )        );        //Recupera produtos para empresa        recuperarProdutos();        recuperarDadosUsuario();    }    private void confirmarQuantidade( final int posicao){        AlertDialog.Builder builder = new AlertDialog.Builder(this);        builder.setTitle("Quantidade");        builder.setMessage("Digite a quantidade");        final EditText editQuantidade = new EditText(this);        editQuantidade.setText("1");        builder.setView( editQuantidade );        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialog, int which) {                String quantidade = editQuantidade.getText().toString();                Produto produtoSelecionado = produtos.get(posicao);                ItemPedido itemPedido = new ItemPedido();                itemPedido.setIdProduto( produtoSelecionado.getIdProduto() );                itemPedido.setNomeProduto( produtoSelecionado.getNome() );                itemPedido.setPreco( produtoSelecionado.getPreco() );                itemPedido.setQuantidade( Integer.parseInt(quantidade) );                itensCarrinho.add( itemPedido );                if( pedidoRecuperado == null ){                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);                }                pedidoRecuperado.setNome( usuario.getNome() );                pedidoRecuperado.setEndereco( usuario.getEndereco() );                pedidoRecuperado.setTelefoneUsuario(usuario.getTelefoneUsuario());                pedidoRecuperado.setItens(itensCarrinho);                pedidoRecuperado.salvar();            }        });        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialog, int which) {            }        });        AlertDialog dialog = builder.create();        dialog.show();    }    private void recuperarDadosUsuario() {        dialog = new SpotsDialog.Builder()                .setContext(this)                .setMessage("Carregando dados")                .setCancelable( false )                .build();        dialog.show();        DatabaseReference usuariosRef = firebaseRef                .child("usuarios")                .child( idUsuarioLogado );        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {            @Override            public void onDataChange(DataSnapshot dataSnapshot) {                if( dataSnapshot.getValue() != null ){                    usuario = dataSnapshot.getValue(Usuario.class);                }                recuperPedido();            }            @Override            public void onCancelled(DatabaseError databaseError) {            }        });    }    private void recuperPedido() {        DatabaseReference pedidoRef =firebaseRef                .child("pedidos_usuario")                .child(idEmpresa)                .child(idUsuarioLogado);        pedidoRef.addValueEventListener(new ValueEventListener() {            @Override            public void onDataChange(DataSnapshot dataSnapshot) {                // teste se tem pedido                qtItensCarrinho=0;                totalCarrinho=0.0;                itensCarrinho= new ArrayList<>();                if(dataSnapshot.getValue()!=null){pedidoRecuperado= dataSnapshot.getValue(Pedido.class);itensCarrinho=pedidoRecuperado.getItens();for(ItemPedido itens: itensCarrinho){int qtde= itens.getQuantidade();Double preco = itens.getPreco();totalCarrinho += (qtde*preco);qtItensCarrinho +=qtde;}                }                DecimalFormat df = new DecimalFormat("0.00");               textCarrinhoQuantidade.setText("qtd:"+String.valueOf(qtItensCarrinho));                textCarrinhoTotal.setText(df.format(totalCarrinho));                dialog.dismiss();            }            @Override            public void onCancelled(DatabaseError databaseError) {            }        });    }    private void recuperarProdutos(){        DatabaseReference produtosRef = firebaseRef                .child("produtos")                .child( idEmpresa );        produtosRef.addValueEventListener(new ValueEventListener() {            @Override            public void onDataChange(DataSnapshot dataSnapshot) {                produtos.clear();                for (DataSnapshot ds: dataSnapshot.getChildren()){                    produtos.add( ds.getValue(Produto.class) );                }                adapterProduto.notifyDataSetChanged();            }            @Override            public void onCancelled(DatabaseError databaseError) {            }        });    }    @Override    public boolean onCreateOptionsMenu(Menu menu) {        MenuInflater inflater = getMenuInflater();        inflater.inflate(R.menu.menu_lista_de_pedidos, menu);        return super.onCreateOptionsMenu(menu);    }    @Override    public boolean onOptionsItemSelected(MenuItem item) {        switch (item.getItemId()){            case R.id.menuPedido :                 confirmarPedido();                break;        }        return super.onOptionsItemSelected(item);    }    private void confirmarPedido() {        AlertDialog.Builder builder = new AlertDialog.Builder(this);        builder.setTitle("Selecione um método de pagamento");        CharSequence[] itens = new CharSequence[]{                "Dinheiro", "Máquina cartão"        };        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialog, int which) {                metodoPagamento = which;            }        });        final EditText editObservacao = new EditText(this);        editObservacao.setHint("Digite uma observação");        builder.setView( editObservacao );        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialog, int which) {                String observacao = editObservacao.getText().toString();                pedidoRecuperado.setMetodoPagamento( metodoPagamento );                pedidoRecuperado.setObservacao( observacao );                pedidoRecuperado.setStatus("confirmado");                pedidoRecuperado.confirmar();                pedidoRecuperado.remover();                pedidoRecuperado = null;            }        });        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialog, int which) {            }        });        AlertDialog dialog = builder.create();        dialog.show();    }    private void inicializarComponentes(){        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutoCardapio);        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);        textCarrinhoQuantidade=findViewById(R.id.textCarrinhoQuantidade);        textCarrinhoTotal=findViewById(R.id.textCarrinhoTotal);    }}