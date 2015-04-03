package dialog.ds;

#if stencyl

typedef Animation = scripts.ds.dialog.Animation;
typedef RatioInt = scripts.ds.dialog.RatioInt;
typedef RatioPoint = scripts.ds.dialog.RatioPoint;
typedef ScalingImageTemplate = scripts.ds.dialog.ScalingImageTemplate;
typedef Style = scripts.ds.dialog.Style;
typedef TweenTemplate = scripts.ds.dialog.TweenTemplate;
typedef WindowTemplate = scripts.ds.dialog.WindowTemplate;

#elseif unity

//typedef Animation = scripts.ds.dialog.Animation;
typedef RatioInt = dialog.unity.ds.RatioInt;
typedef RatioPoint = dialog.unity.ds.RatioPoint;
typedef ScalingImageTemplate = dialog.unity.ds.ScalingImageTemplate;
typedef Style = dialog.unity.ds.Style;
typedef TweenTemplate = dialog.unity.ds.TweenTemplate;
typedef WindowTemplate = dialog.unity.ds.WindowTemplate;

#end
