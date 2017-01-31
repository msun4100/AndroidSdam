package kr.me.sdam.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import kr.me.sdam.BgImage;
import kr.me.sdam.R;
import kr.me.sdam.tabthree.TabThreeResult;

/**
 * Created by KMS on 2017-01-08.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder{
    Context mContext;

    TextView contentView;

    ImageView iconDistanceView;
    TextView distanceView;
    ImageView iconTimeView;
    TextView timeView;
    ImageView iconReplyView;
    TextView replyCountView;

    ImageView iconLikeView;
    TextView likeCountView;

    ImageView iconEmotionView;
    ImageView background;

    View frameDistanceView;
    View frameReplyView;
    View frameLikeView;
    View layoutView;

    FrameLayout reportView;

    CommonResult mItem;

    public OnItemClickListener itemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public OnItemLongClickListener itemLongClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        itemLongClickListener = listener;
    }

    public ItemViewHolder(View itemView, Context context) {
        super(itemView);
        this.mContext = context;
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                if(itemLongClickListener != null){
                    itemLongClickListener.onItemLongClick(v, getAdapterPosition());
                }
                return true;
            }
        });
        layoutView = (View) itemView.findViewById(R.id.frame_common_list);
        contentView = (TextView) itemView.findViewById(R.id.text_content);
        iconDistanceView = (ImageView) itemView.findViewById(R.id.image_distance);
        distanceView = (TextView) itemView.findViewById(R.id.text_distance);
        iconTimeView = (ImageView) itemView.findViewById(R.id.image_time);
        timeView = (TextView) itemView.findViewById(R.id.text_time);
        iconReplyView = (ImageView) itemView.findViewById(R.id.image_reply);
        replyCountView = (TextView) itemView.findViewById(R.id.text_reply_count);
        iconLikeView = (ImageView) itemView.findViewById(R.id.image_like);
        likeCountView = (TextView) itemView.findViewById(R.id.text_like_count);

        iconEmotionView = (ImageView) itemView.findViewById(R.id.image_emoticon);
        background = (ImageView)itemView.findViewById(R.id.image_bg_);

        frameDistanceView = (View) itemView.findViewById(R.id.frame_distance);
        frameReplyView = (View) itemView.findViewById(R.id.frame_reply);
        frameLikeView = (View) itemView.findViewById(R.id.frame_like);
        reportView = (FrameLayout) itemView.findViewById(R.id.fl_report);

        frameDistanceView.setOnClickListener(viewListener);
        frameReplyView.setOnClickListener(viewListener);
        frameLikeView.setOnClickListener(viewListener);
        reportView.setOnClickListener(viewListener);

    }

    public void setChildItem(CommonResult item) {
        mItem = item;

        if(item.locate==99999){
            distanceView.setText("어딘가");
        } else {
            distanceView.setText("" + item.locate + "km");// int
        }
        if(item.timeStamp != null){
            String timeStr="time";
            if(item.timeStamp.time.equals("hours")){
                timeStr = "시간 전";
            } else if(item.timeStamp.time.equals("dates")){
                timeStr = "일 전";
            } else if(item.timeStamp.time.equals("minutes")){
                timeStr = "분 전";
            } else if(item.timeStamp.time.equals("second")){
                timeStr = "초 전";
            } else if (item.timeStamp.time.equals("months")) {
                timeStr = "달 전";
            } else if (item.timeStamp.time.equals("years")){
                timeStr = "년 전";
            }
            timeView.setText(item.timeStamp.value + timeStr);
        }
        contentView.setText(item.content);

        replyCountView.setText("" + item.repNum);// int
        likeCountView.setText("" + item.goodNum);// int

        iconLikeView.setImageResource(R.drawable.b_main_view_contents_icon_05_off);

        if (item.myGood == 0 ) {
            iconLikeView .setImageResource(R.drawable.b_main_view_contents_icon_05_off);
        } else if(item.myGood >= 1 ){
            iconLikeView .setImageResource(R.drawable.b_main_view_contents_icon_05_on);
        } else {
            iconLikeView .setImageResource(R.drawable.b_main_view_contents_icon_05_on);
        }


        iconEmotionView.setImageResource( BgImage.getEmotionIcon(item.emotion) );
        int bgResource = BgImage.DEFAULT_IMAGE;
        bgResource = BgImage.getBgImage(Integer.parseInt(item.category) , Integer.parseInt(item.image));
        background.setImageResource(bgResource);
    }

    public View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fl_report:
                    if (mListener != null) {
                        mListener.onLikeClick(v, getAdapterPosition(), mItem, CommonResult.TYPE_REPORT);
                    }
                    break;
                case R.id.frame_distance:
                    if (mListener != null) {
                        mListener.onLikeClick(v, getAdapterPosition(), mItem, CommonResult.TYPE_DISTANCE);
                    }
                    break;
                case R.id.frame_reply:
                    if (mListener != null) {
                        mListener.onLikeClick(v, getAdapterPosition(), mItem, CommonResult.TYPE_REPLY);
                    }
                    break;
                case R.id.frame_like:
                    if (mListener != null) {
                        mListener.onLikeClick(v, getAdapterPosition(), mItem, CommonResult.TYPE_LIKE);
                    }
                    break;
                default:
                    break;
            }

        }
    };

    //for individual row item button click
    public interface OnLikeClickListener {
        public void onLikeClick(View v, int position, CommonResult item, int type);
    }
    OnLikeClickListener mListener;
    public void setOnLikeClickListener(OnLikeClickListener listener) {
        mListener = listener;
    }
}
