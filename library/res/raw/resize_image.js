//document.getElementsByName("viewport")[0].content = "width=%s, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no";
var myImg;
var maxWidth = 300;
for(i = 0; i < document.images.length; i++){
	myImg = document.images[i];
	if(myImg.width > maxWidth){
		myImg.style.width = '95%%';
		myImg.style.height = 'auto';
	}
}