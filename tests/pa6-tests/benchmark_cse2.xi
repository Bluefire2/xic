use io
use conv
//tests dead code elim with evil variable names
main(args:int[][]){
rsp:int=1000000000;
a:int=100;
b:int=100;
c:int=1;
while(rsp>0){
rbp:int= a + b * c;
rsp=rsp-1;
}}
