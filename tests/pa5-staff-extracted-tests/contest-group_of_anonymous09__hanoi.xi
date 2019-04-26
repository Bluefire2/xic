use io move(n:int, from:int, to:int, via:int) { if ( n > 0 ) {
move(n - 1, from, via, to); print("Move disk from pole ")
print(itoa(from)) print(" to ") print(itoa(to)); print(".\n")
move(n - 1, via, to, from) } }
main(args:int[][]) { move(4, 1, 2, 3) }
itoa(n:int) : int[] { s:int[digits(n)] i:int sign:int sign = n
if(n < 0) n = -n i = 0 s[i] = n % 10 + '0'; n = n / 10 while(n > 0) {
i = i + 1 s[i] = n % 10 + '0' n = n / 10 } reverse(s) return(s) } 
reverse(s:int[]) { i:int j:int c:int i = 0 j = length(s) - 1 while(i<j) {
c = s[i] s[i] = s[j] s[j] = c; i = i + 1 j = j - 1; } } 
digits(d:int) : int { tmp:int = d; dec:int if(d > 0) dec = 0 else dec = 1
while(tmp >= 1 | tmp <= -1) { tmp = tmp / 10 dec = dec + 1 } return dec }

// Uses only the io interface
// Handwritten conv replacement functions
// Expected output:

// Move disk from pole 1 to 3.
// Move disk from pole 1 to 2.
// Move disk from pole 3 to 2.
// Move disk from pole 1 to 3.
// Move disk from pole 2 to 1.
// Move disk from pole 2 to 3.
// Move disk from pole 1 to 3.
// Move disk from pole 1 to 2.
// Move disk from pole 3 to 2.
// Move disk from pole 3 to 1.
// Move disk from pole 2 to 1.
// Move disk from pole 3 to 2.
// Move disk from pole 1 to 3.
// Move disk from pole 1 to 2.
// Move disk from pole 3 to 2.
