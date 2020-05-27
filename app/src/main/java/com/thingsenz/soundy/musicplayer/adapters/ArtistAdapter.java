package com.thingsenz.soundy.musicplayer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.thingsenz.soundy.R;
import com.thingsenz.soundy.musicplayer.models.Artist;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder>{
    List<Artist> artists;
    Context context;

    public ArtistAdapter(List<Artist> artists, Context context) {
        this.artists = artists;
        this.context = context;
    }

    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_artist,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ArtistAdapter.ViewHolder holder, int position) {
        Artist artistModel = artists.get(position);
        Picasso.with(context).load(artistModel.getArtistImage()).placeholder(R.drawable.default_album_icon).into(holder.artistImage);
        holder.artistName.setText(artistModel.getArtistName());
        holder.songCount.setText(artistModel.getSongCount()+"");
        holder.albumCount.setText(artistModel.getAlbumCount()+"");
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView artistImage;
        TextView artistName;
        TextView songCount;
        TextView albumCount;
        public ViewHolder(View itemView) {
            super(itemView);
            artistImage = (ImageView)itemView.findViewById(R.id.iv_artist_coverart);
            artistName = (TextView)itemView.findViewById(R.id.tv_artist_name);
            songCount = (TextView)itemView.findViewById(R.id.tv_artist_song_count);
            albumCount = (TextView)itemView.findViewById(R.id.tv_artist_album_count);
        }
    }
}