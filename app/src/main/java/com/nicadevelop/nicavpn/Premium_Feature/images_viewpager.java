package com.nicadevelop.nicavpn.Premium_Feature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.nicadevelop.nicavpn.R;

public class images_viewpager extends PagerAdapter {

    private Context context;
    private Integer[] images = {R.drawable.iap_ic_fast, R.drawable.iap_ic_no_ads,
            R.drawable.iap_ic_secure, R.drawable.iap_ic_anonymous,R.drawable.iap_ic_unlimited,
            R.drawable.iap_ic_worldwide, R.drawable.iap_ic_no_logs};
    
    private String[] titleString = {"Fast","Remove Ads","Secure","Anonymous",
            "Unlimited","VIP Server", "No Logs"};
    private String[] descString = {"Upto 100 MBPS bandwidth to explore the world"
            ,"Have fun surfing without annoying ads"
            ,"Transfer obfuscate traffic via encrypted tunnel"
            ,"Hide your IP, anonymous surfing"
            ,"Get unlimited bandwidth, speed and traffic"
            ,"More server arround the world"
            ,"We will never store or share your access logs"};

    public images_viewpager(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.premium_viewpager, null);
    
        ImageView mImage = view.findViewById(R.id.pager_image);
        TextView mTitle = view.findViewById(R.id.pager_title_txt);
        TextView mDescription = view.findViewById(R.id.pager_desc_txt);
        mImage.setImageResource(images[position]);
        mTitle.setText(titleString[position]);
        mDescription.setText(descString[position]);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}