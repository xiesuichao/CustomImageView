# CustomImageView
圆形、全圆角、部分圆角、边框  
[![](https://jitpack.io/v/xiesuichao/CustomImageView.svg)](https://jitpack.io/#xiesuichao/CustomImageView)  

自定义ImageView:
支持圆形，全圆角，部分圆角，加边框，边框是否覆盖在内容上  
支持Glide显示gif  
保留原生ImageView的所有功能，

//设置圆形  
<com.frost.defense.customimageview.CustomImageView  
    android:id="@+id/iv_material_expert_avatar"  
    android:layout_width="@dimen/dp_56"  
    android:layout_height="@dimen/dp_56"  
    tools:src="@drawable/common_default_avatar"  
    />

//设置圆角  
<com.frost.defense.customimageview.CustomImageView  
    android:id="@+id/iv_user_home_avatar"  
    android:layout_width="@dimen/dp_70"  
    android:layout_height="@dimen/dp_70"  
    app:civCornerRadius="8dp"  
    tools:src="@drawable/common_default_avatar"  
    />