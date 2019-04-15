	.file	"fact.c"
	.intel_syntax noprefix
	.text
	.globl	_Ifactorial
	.type	_Ifactorial, @function
_Ifactorial:
	push	rbp
	mov	rbp, rsp
	sub	rsp, 16
	mov	QWORD PTR -8[rbp], rdi
	cmp	QWORD PTR -8[rbp], 1
	jg	.L2
	mov	eax, 1
	jmp	.L3
.L2:
	mov	rax, QWORD PTR -8[rbp]
	sub	rax, 1
	mov	rdi, rax
	call	_Ifactorial
	imul	rax, QWORD PTR -8[rbp]
.L3:
	leave
	ret
	.size	_Ifactorial, .-_Ifactorial
	.globl	prompt
	.data
	.align 32
	.type	prompt, @object
	.size	prompt, 64
prompt:
	.quad	7
	.quad	78
	.quad	117
	.quad	109
	.quad	98
	.quad	101
	.quad	114
	.quad	63
	.globl	is
	.align 32
	.type	is, @object
	.size	is, 48
is:
	.quad	5
	.quad	33
	.quad	32
	.quad	105
	.quad	115
	.quad	32
	.text
	.globl	_Imain_paai
	.type	_Imain_paai, @function
_Imain_paai:
	push	rbp
	mov	rbp, rsp
	sub	rsp, 64
	mov	QWORD PTR -56[rbp], rdi
	jmp	.L5
.L6:
	lea	rax, prompt[rip+8]
	mov	rdi, rax
	call	_Iprint_pai@PLT
	call	_Ireadln_ai@PLT
	mov	QWORD PTR -16[rbp], rax
	mov	rax, QWORD PTR -16[rbp]
	mov	rdi, rax
	call	_IparseInt_t2ibai@PLT
	mov	QWORD PTR -24[rbp], rax
	mov	rax, QWORD PTR -8[rbp]
#APP
# 30 "fact.c" 1
	movq %rdx, rax
# 0 "" 2
#NO_APP
	mov	QWORD PTR -8[rbp], rax
	cmp	QWORD PTR -8[rbp], 0
	je	.L5
	mov	rax, QWORD PTR -24[rbp]
	mov	rdi, rax
	call	_Ifactorial
	mov	QWORD PTR -32[rbp], rax
	mov	rax, QWORD PTR -24[rbp]
	mov	rdi, rax
	call	_IunparseInt_aii@PLT
	mov	QWORD PTR -40[rbp], rax
	lea	rax, is[rip+8]
	mov	rdi, rax
	call	_Iprint_pai@PLT
	mov	rax, QWORD PTR -32[rbp]
	mov	rdi, rax
	call	_IunparseInt_aii@PLT
	mov	QWORD PTR -40[rbp], rax
	mov	rax, QWORD PTR -40[rbp]
	mov	rdi, rax
	call	_Iprintln_pai@PLT
.L5:
	call	_Ieof_b@PLT
	test	rax, rax
	je	.L6
	nop
	leave
	ret
	.size	_Imain_paai, .-_Imain_paai
	.ident	"GCC: (Ubuntu 7.3.0-27ubuntu1~18.04) 7.3.0"
	.section	.note.GNU-stack,"",@progbits
