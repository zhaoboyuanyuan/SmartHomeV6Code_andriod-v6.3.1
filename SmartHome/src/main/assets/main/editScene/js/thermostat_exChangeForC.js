function exChange(changeNum) {
    var resultNum = changeNum;
    var arry1 = new　Array();
    var arry2 = new　Array();
    for(var i=10;i<=32;i+=0.5){
        arry1.push(i);
    }
    for(var j=50;j<=55;j++){
        arry2.push(j);
    }
    for(var k=55;k<=64;k++){
        arry2.push(k);
    }
    for(var l=64;l<=73;l++){
        arry2.push(l);
    }
    for(var m=73;m<=82;m++){
        arry2.push(m);
    }
    for(var n=82;n<=90;n++){
        arry2.push(n);
    }
    var FIndex = $.inArray(changeNum, arry2);
    if(FIndex>=0){
        resultNum = arry1[FIndex];
    }
    var CIndex = $.inArray(changeNum, arry1);
    if(CIndex>=0){
        resultNum = arry2[CIndex];
    }
    return resultNum;
}