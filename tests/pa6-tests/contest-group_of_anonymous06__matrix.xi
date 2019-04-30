use io; use conv
main(args:int[][]){
	m:int[5][2]
	i:int=0; 	j:int=0; 	s:int=0
	while(i<length(m)){
		j=0
		while(j<length(m[0])){
			m[i][j]=3
			j=j+1
		} i=i+1
	} i=0
	while(i<length(m)){
		j=0
		while(j<length(m[0])){
			s=s+m[i][j]*10000000000000000
			j=j+1
		}
		i=i+1
	}
	println("sum should be 300000000000000000, and is " + unparseInt(s))
}
