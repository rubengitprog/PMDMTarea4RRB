package dam.pmdm.spyrothedragon.adapters;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;
import dam.pmdm.spyrothedragon.views.FlameView;

import java.util.List;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder> {

    private List<Character> list;

    public CharactersAdapter(List<Character> charactersList) {
        this.list = charactersList;
    }

    @Override
    public CharactersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CharactersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharactersViewHolder holder, int position) {
        Character character = list.get(position);
        holder.nameTextView.setText(character.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(character.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        // Detectar la pulsación larga solo en el primer ítem (posición 0)
        if (position == 0) {
            holder.itemView.setOnLongClickListener(v -> {
                // Hacer visible la llama cuando se pulsa largo sobre Spyro
                holder.flameView.setVisibility(View.VISIBLE);  // Hacerlo visible
                holder.flameView.animateFlame();  // Llama la animación
                // Retorna true para indicar que el evento ha terminado correctamente
                // Reproducir sonido
                MediaPlayer mediaPlayer = MediaPlayer.create(holder.flameView.getContext(), R.raw.spyrofiresound);
                mediaPlayer.start();
                return true;
            });
        } else {
            // Aseguramos que el primer item no reciba este listener
            holder.itemView.setOnLongClickListener(null);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CharactersViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;
        FlameView flameView;  // Añadido para el cono de la llama

        public CharactersViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
            flameView = itemView.findViewById(R.id.flameView);  // Referencia a FlameView
        }
    }

}
