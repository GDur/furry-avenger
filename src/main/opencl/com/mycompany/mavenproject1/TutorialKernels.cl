__kernel void m_mult(
    __global const float* a,
    __global const float* b, 
    __global float* out, 
    int n)
{
    int i = get_global_id(0);
    if (i >= n)
        return;

    out[i] = 0.03;
}
__kernel void add_floats(
    __global const float* a,
    __global const float* b, 
    __global float* out, 
    int n)
{
    int i = get_global_id(0);
    if (i >= n)
        return;

    out[i] = a[i] + b[i];
}

__kernel void fill_in_values(__global float* a, __global float* b, int n) 
{
    int i = get_global_id(0);
    if (i >= n)
        return;

    a[i] = cos((float)i);
    b[i] = sin((float)i);
}