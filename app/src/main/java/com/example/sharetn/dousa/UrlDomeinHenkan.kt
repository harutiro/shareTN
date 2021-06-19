package com.example.sharetn.dousa

class UrlDomein{

    fun hen(moto:String): String {

        var httpsCheck = ""
        var domein = ""
        var surassyu = ""

        var charAry = moto.toCharArray()

        for (ch in charAry){

            when (ch){

                'h' -> {
                    if(httpsCheck == ""){

                        httpsCheck += ch

                    }else{

                        if(surassyu == "//"){
                            domein += ch
                        }

                    }

                }
                't' ->{

                    if(httpsCheck == "h"){

                        httpsCheck += ch

                    }else if(httpsCheck == "ht") {

                        httpsCheck += ch

                    }else{

                        if(surassyu == "//"){
                            domein += ch
                        }

                    }

                }
                'p' ->{
                    if(httpsCheck == "htt"){

                        httpsCheck += ch

                    }else{

                        if(surassyu == "//"){
                            domein += ch
                        }

                    }

                }
                's' ->{
                    if(httpsCheck == "http" && surassyu ==""){

                        httpsCheck += ch

                    }else{

                        if(surassyu == "//"){
                            domein += ch
                        }

                    }

                }

                '/' ->{
                    if((httpsCheck == "https" || httpsCheck == "http") && (surassyu == "" || surassyu == "/")){

                        surassyu += ch

                    }else{

                        if(surassyu == "//"){
                            break
                        }

                    }

                }

                ':' ->{}

                else ->{
                    if(httpsCheck == "https" || httpsCheck == "http"){

                        domein += ch

                    }else{

                    }
                }


            }



//            https://www.google.com/s2/favicons?domain=


        }


        return domein

    }

    fun check(moto: String): Boolean {

        var httpsCheck = ""
        var domein = ""
        var surassyu = ""

        val charAry = moto.toCharArray()

        for (ch in charAry){

            when (ch){

                'h' -> {
                    if(httpsCheck == ""){

                        httpsCheck += ch

                    }

                }
                't' ->{

                    if(httpsCheck == "h"){

                        httpsCheck += ch

                    }else if(httpsCheck == "ht") {

                        httpsCheck += ch

                    }

                }
                'p' ->{
                    if(httpsCheck == "htt"){

                        httpsCheck += ch

                    }

                }
                's' ->{
                    if(httpsCheck == "http" && surassyu ==""){

                        httpsCheck += ch

                    }

                }

                '/' ->{
                    if((httpsCheck == "https" || httpsCheck == "http") && (surassyu == "" )){

                        surassyu += ch

                    }else if(surassyu == "/"){

                        surassyu += ch
                        break

                    }

                }

                ':' ->{}

                else ->{

                }


            }





//            https://www.google.com/s2/favicons?domain=


        }

        return (httpsCheck == "https" || httpsCheck == "http") && (surassyu == "//" )



    }
}