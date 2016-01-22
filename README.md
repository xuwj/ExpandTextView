# ExpandTextView
A Expand TextView which like WeChat circle of friends.You can use in your app community.

# Attrs
```
<attr name="content_textcolor" format="color" />
<attr name="content_textsize" format="dimension" />
<attr name="text" format="string" />
<attr name="max_lines" format="integer" />
<attr name="more_text_color" format="color" />
<attr name="is_load_anim" format="boolean" />
<attr name="is_load_anim_time" format="integer" />
```

# Usage
Add below code in your '.xml' file.
```
<com.xwj.library.ExpandTextView
  android:id="@+id/etv_expand"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:paddingLeft="10dp"
  android:paddingRight="10dp"
  app:text="@string/str_test"
  app:is_load_anim="true"/>
```

# Screenshots
![](http://7xj4l6.com1.z0.glb.clouddn.com/expand_textview.gif)
