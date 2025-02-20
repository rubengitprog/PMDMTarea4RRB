package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Collectible;
import dam.pmdm.spyrothedragon.ui.CollectiblesFragment;
import dam.pmdm.spyrothedragon.VideoActivity;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    private List<Collectible> list;
    private Context context; // Necesitamos el contexto para el Toast
    private int easterEgg = 0 ;
    private CollectiblesFragment collectiblesFragment;

    public CollectiblesAdapter(List<Collectible> collectibleList, Context context) {
        this.list = collectibleList;
        this.context = context;
    }

    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());

        // Cargar la imagen
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        // Detectar clic en el segundo ítem (índice 1)
        holder.itemView.setOnClickListener(v -> {
            // Incrementar easterEgg
            if(position==1){
                easterEgg++;
                // Reproducir sonido click.mp3
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.click);  // Asegúrate de tener el archivo click.mp3 en la carpeta res/raw
                mediaPlayer.start();
                if(easterEgg==4){
                    context.startActivity(new Intent(context, VideoActivity.class));
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CollectiblesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}
