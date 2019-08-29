/*
 * num: 0.只有标题，1.只有一行文字，2.只有多行文字，3.有标题+1行文字，4.有标题+多行文字 5.标题+输入框
 * titTxt:表示标题文字
 * txt:表示文字
 * isClick:0.表示无按钮，1.表示只有一个按钮，2.表示有2个按钮
 * aTxt1:表示按钮1的文字
 * aTxt2:表示按钮2的文字
 * placeTxt:表示输入框placeholder的值
 */
function editPopup(data){
    //外层section
    var sect = document.createElement("section")
    sect.className = "sect_content"
    sect.style.display = "block"
    sect.style.position = "fixed"
    sect.style.left = "0"
    sect.style.top = "0"
    sect.style.zIndex = "1000"
    sect.style.width = "100%"
    sect.style.height = "100%"
    sect.style.backgroundColor = "rgba(0,0,0,0.4)"
    document.body.appendChild(sect)

    //section中的div.popup
    var div = document.createElement("div")
    div.style.position = "absolute"
    div.style.left = "15%"
    div.style.top = "30%"
    div.style.width = "70%"
    div.style.zIndex = "1001"
    div.style.overflow = "hidden"
    div.style.backgroundColor = "#fff"
    // div.style.transform = "translateY(-50%)"
    div.style.borderRadius = "5px"
    div.className = "popup"
    div.style.boxSizing = "border-box";
    document.getElementsByClassName("sect_content")[0].appendChild(div)

    //标题
    var title = document.createElement("h4")
    title.style.width = "100%"
    title.style.height = "4rem"
    title.style.lineHeight = "4rem"
    title.style.textAlign = "center"
    title.style.padding = "0 3%"
    title.style.marginTop = "1rem"
    title.style.fontWeight = "500"
    title.style.fontSize = "1.6rem"
    title.style.boxSizing = "border-box";
    title.innerHTML = data.titTxt

    //文字
    var txt = document.createElement("span")
    txt.style.display = "block"
    txt.style.width = "100%"
    txt.style.textAlign = "left"
    txt.style.fontSize = "1.4rem"
    txt.style.color = "#999"
    txt.style.padding = "0 5%"
    txt.style.boxSizing = "border-box";
    txt.innerHTML = data.txt

    var divInput = document.createElement("div")
    divInput.style.position = "relative"
    divInput.style.overflow = "hidden"
    divInput.className = "inputDiv"
    divInput.style.boxSizing = "border-box";
    var input = document.createElement("input")
    input.type = "text"
    input.style.width = "90%"
    input.style.height = "4rem"
    input.style.border = "1px solid #ddd"
    input.style.outline = "none"
    input.style.margin = "1rem 5%"
    input.style.padding = "0 10% 0 3%"
    input.style.fontSize = "1.6rem"
    input.placeholder = data.placeTxt
    input.maxLength = "20"
    input.style.boxSizing = "border-box";
    var i = document.createElement("i")
    i.style.position = "absolute"
    i.style.right = "8%"
    i.style.top = "2.5rem"
    i.style.width = "1rem"
    i.style.height = "1rem"
    i.style.background = "url(../fonts/del.png) no-repeat center center"
    i.style.backgroundSize = "1rem auto"
    i.style.boxSizing = "border-box";

    //有标题 data.num为0表示有标题 data.titTxt表示为文字
    if(data.num == 0){
        document.getElementsByClassName("popup")[0].appendChild(title)
        //有标题data.num为1表示只有一行文字 data.txt表示为文字
    }else if(data.num == 1){
        document.getElementsByClassName("popup")[0].appendChild(txt)
        txt.className = "one_row"
        document.getElementsByClassName("one_row")[0].style.height = "3rem"
        document.getElementsByClassName("one_row")[0].style.lineHeight = "3rem"
        if(data.isClick != 0){
            document.getElementsByClassName("one_row")[0].style.marginTop = "1rem"
        }
        //有标题data.num为2表示有多行文字无标题 data.txt表示为文字
    }else if(data.num == 2){
        document.getElementsByClassName("popup")[0].appendChild(txt)
        txt.className = "two_row"
        document.getElementsByClassName("two_row")[0].style.lineHeight = "2rem"
        document.getElementsByClassName("two_row")[0].style.marginTop = "2rem"
        //有标题data.num为3表示有1行文字有标题 data.txt表示为文字
    }else if(data.num == 3){
        document.getElementsByClassName("popup")[0].appendChild(title)
        document.getElementsByClassName("popup")[0].appendChild(txt)
        txt.className = "one_row"
        document.getElementsByClassName("one_row")[0].style.height = "3rem"
        document.getElementsByClassName("one_row")[0].style.lineHeight = "3rem"
        //有标题data.num为3表示有多行文字有标题 data.txt表示为文字
    }else if(data.num == 4){
        document.getElementsByClassName("popup")[0].appendChild(title)
        document.getElementsByClassName("popup")[0].appendChild(txt)
        txt.className = "two_row"
        document.getElementsByClassName("two_row")[0].style.lineHeight = "2rem"
    }else if(data.num == 5){
        document.getElementsByClassName("popup")[0].appendChild(title)
        document.getElementsByClassName("popup")[0].appendChild(divInput)
        document.getElementsByClassName("inputDiv")[0].appendChild(input)
        document.getElementsByClassName("inputDiv")[0].appendChild(i)
    }

    var btn = document.createElement("div")
    btn.style.marginTop = "1rem"
    btn.style.borderTop = "1px solid #ddd"
    btn.style.height = "4.4rem"
    btn.className = "alertBtn"

    var a = document.createElement("a")
    a.style.display = "block"
    a.style.height = "4.4rem"
    a.style.lineHeight = "4.4rem"
    a.style.textAlign = "center"
    a.style.fontSize = "1.6rem"
    a.style.color = "#000"
    a.style.textDecoration = "none"

    var a1 = document.createElement("a")
    a1.style.display = "block"
    a1.style.height = "4.4rem"
    a1.style.lineHeight = "4.4rem"
    a1.style.textAlign = "center"
    a1.style.fontSize = "1.6rem"
    a1.style.color = "#000"
    a1.style.textDecoration = "none"

    if(data.isClick != 0){
        document.getElementsByClassName("popup")[0].appendChild(btn)
        var btnClass = document.getElementsByClassName("alertBtn")[0]
        //一个按钮
        if(data.isClick == 1){
            btnClass.appendChild(a)
            btnClass.getElementsByTagName("a")[0].style.width = "100%"
            btnClass.getElementsByTagName("a")[0].innerHTML = data.aTxt1
        }else{
            btnClass.appendChild(a)
            btnClass.appendChild(a1)
            btnClass.getElementsByTagName("a")[0].style.borderRight = "1px solid #ddd"
            btnClass.getElementsByTagName("a")[0].style.width = "50%"
            btnClass.getElementsByTagName("a")[0].style.float = "left"
            btnClass.getElementsByTagName("a")[0].style.boxSizing = "border-box"
            btnClass.getElementsByTagName("a")[1].style.width = "50%"
            btnClass.getElementsByTagName("a")[1].style.float = "left"
            btnClass.getElementsByTagName("a")[1].style.boxSizing = "border-box"
            btnClass.getElementsByTagName("a")[0].innerHTML = data.aTxt1
            btnClass.getElementsByTagName("a")[1].innerHTML = data.aTxt2
        }
    }else{
        document.getElementsByTagName("span")[0].style.marginBottom = "1rem"
    }
}

