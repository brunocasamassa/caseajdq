package studio.brunocasamassa.ajudaquioficial.adapters;

/**
 * Created by bruno on 09/05/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.gujun.android.taggroup.TagGroup;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;

public class RecyclerAdapterPedidos extends RecyclerView.Adapter {

    private ArrayList<Pedido> pedidos;
    private Context context;

    public RecyclerAdapterPedidos( Context c , ArrayList<Pedido> objects) {
        this.pedidos = objects;
        this.context = c;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.model_pedido, parent, false);
        PedidosViewHolder holder = new PedidosViewHolder(view);



        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                PedidosViewHolder pedidosHolder = (PedidosViewHolder) holder;

                Pedido pedido = pedidos.get(position);

                pedidosHolder.nomePedido.setText(pedido.getTitulo());
                pedidosHolder.descricao.setText(pedido.getDescricao());
                pedidosHolder.tags.setTags(pedido.getTagsCategoria());
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }
}

class PedidosViewHolder extends RecyclerView.ViewHolder {


    final TextView nomePedido;
    final TextView descricao;
    final TagGroup tags;

    public PedidosViewHolder(View view) {
        super(view);
        nomePedido = (TextView) view.findViewById(R.id.nomePedido);
        descricao = (TextView) view.findViewById(R.id.descricao_pedido);
        tags = (TagGroup) view.findViewById(R.id.tagPedidos);

    }


}