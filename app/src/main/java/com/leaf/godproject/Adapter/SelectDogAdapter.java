package com.leaf.godproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.leaf.godproject.Model.Dog;
import com.leaf.godproject.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectDogAdapter extends RecyclerView.Adapter<SelectDogAdapter.ViewHolder>{
    private Context mContext;
    private List<Dog> mDog;
    private FirebaseUser firebaseUser;
    FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private String dogid;

    public SelectDogAdapter(Context context, List<Dog> dogs){
        mContext = context;
        mDog = dogs;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mydog_recycle_items, parent, false);
        return new SelectDogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectDogAdapter.ViewHolder holder, int position) {
        final Dog dog = mDog.get(position);

        Glide.with(mContext).load(dog.getImgurl())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(holder.dIMG);

        String infoname=(!dog.getName().equals("") ?dog.getName():"未知姓名");
        holder.dNAME.setText(infoname);

        if(!dog.getMixtype().equals("無")){
            String type=dog.getType()+" 混 "+dog.getMixtype();
            holder.dTYPE.setText(type);
        }else{
            String type=!dog.getType().equals("") ?dog.getType():"未知品種";
            holder.dTYPE.setText(type);}

        String info=(!dog.getGender().equals("") ?dog.getGender():"未知性別")+ " "+
                (!dog.getBirth().equals("") ?dog.getBirth():"未知生日");
        holder.dDOGINFO.setText(info);


holder.cardView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        dogid=dog.getId();
    }
});


        mFirebaseAuth= FirebaseAuth.getInstance();
        user=mFirebaseAuth.getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();

    }
    @Override
    public int getItemCount() { return mDog.size(); }


    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView dIMG;
        ImageButton imgbt;
        TextView dNAME,dTYPE,dDOGINFO;
        CardView cardView ;
        ViewHolder(View itemView) {
            super(itemView);
            cardView=(CardView)itemView.findViewById(R.id.cardview);
            dNAME = (TextView) itemView.findViewById(R.id.recycle_dogname);
            dTYPE = (TextView) itemView.findViewById(R.id.recycle_dogtype);
            dDOGINFO = (TextView) itemView.findViewById(R.id.recycle_doginfo);
            dIMG=(CircleImageView)itemView.findViewById(R.id.dog_image);
            imgbt=(ImageButton)itemView.findViewById(R.id.editbt);
        }
    }


}
