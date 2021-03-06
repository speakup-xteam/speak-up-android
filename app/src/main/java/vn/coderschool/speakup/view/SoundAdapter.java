package vn.coderschool.speakup.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import vn.coderschool.speakup.R;
import vn.coderschool.speakup.model.Sound;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder> {

    interface SoundClickListener {
        void onSoundClick(Sound sound);
    }

    private List<Sound> mSounds;

    private SoundClickListener listener;

    public SoundAdapter(List<Sound> sounds, SoundClickListener listener) {
        this.mSounds = sounds;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sound, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Sound sound = mSounds.get(position);

        holder.tvSymbol.setText(sound.getSymbol().toString());
        holder.tvHint.setText(sound.getHint().toString());

        holder.tvSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSoundClick(sound);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSounds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_symbol)
        TextView tvSymbol;

        @BindView(R.id.text_hint)
        TextView tvHint;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
