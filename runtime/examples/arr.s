	.file	"arr.c"
	.intel_syntax noprefix
	.text
	.globl	_Imain_paai
	.type	_Imain_paai, @function
_Imain_paai:
	push	rbp
	mov	rbp, rsp
	sub	rsp, 32
	mov	QWORD PTR -24[rbp], rdi
	mov	rax, QWORD PTR -24[rbp]
	mov	rax, QWORD PTR -8[rax]
	mov	QWORD PTR -16[rbp], rax
	mov	QWORD PTR -8[rbp], 0
	jmp	.L2
.L3:
	mov	rax, QWORD PTR -8[rbp]
	lea	rdx, 0[0+rax*8]
	mov	rax, QWORD PTR -24[rbp]
	add	rax, rdx
	mov	rax, QWORD PTR [rax]
	mov	rdi, rax
	call	_Iprintln_pai@PLT
	add	QWORD PTR -8[rbp], 1
.L2:
	mov	rax, QWORD PTR -8[rbp]
	cmp	rax, QWORD PTR -16[rbp]
	jl	.L3
	nop
	leave
	ret
	.size	_Imain_paai, .-_Imain_paai
	.ident	"GCC: (Ubuntu 7.3.0-27ubuntu1~18.04) 7.3.0"
	.section	.note.GNU-stack,"",@progbits
