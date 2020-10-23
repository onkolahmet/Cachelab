# Cachelab
It's a basic cache simulator which takes an image of memory and a memory trace as input, simulates
the hit/miss behavior of a cache memory on this trace, and outputs the total number of hits, misses, and evictions for each
cache type along with the content of each cache at the end.
## Reference Trace Files
The traces subdirectory of the handout directory contains a collection of reference trace files that we will use to
evaluate the correctness of the cache simulator you write. The memory trace files have the following form: <br /> <br />
M 000ebe20, 3, 58a35a <br />
L 000eaa30, 6 <br />
S 0003b020, 7, abb2cdc69bb454 <br />
I 00002010, 6 <br />


Each line denotes one or two memory accesses. The format of each line for I and L:<br />
    operation address, size.<br /><br />
The format of each line for M and S:<br />
    operation address, size, data.<br /><br />
The operation field denotes the type of memory access:
- “I” denotes an instruction load,
- “L” a data load,<br />
- “S” a data store, and<br />
- “M” a data modify (i.e., a data load followed by a data store). <br />
 
 The address field specifies a 32-bit hexadecimal memory address.<br /> <br />
 The size field specifies the number of bytes accessed by the operation.<br /> <br />
 The data field specifies the data bytes stored in the given address.<br /> <br />
# Example Run

![cachelab pdf](https://user-images.githubusercontent.com/62245004/97057487-ff262d80-1593-11eb-8534-0f5a04efbb1f.png)
