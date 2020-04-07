<template>
    <div style="display: inline" @click="handleClick">
        <span v-html="renderVal(val)"></span>
    </div>
</template>

<script>
    export default {
        props : ['val','valueRender'],
        methods : {
            handleClick(){
                this.$emit('value-click')
            },
            renderVal(val){
                return this.valueRender(val)
            }
        }
    }
</script>
