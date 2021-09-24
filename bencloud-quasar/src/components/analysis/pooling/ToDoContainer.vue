<template>
  <div class="todo-container"><slot></slot></div>
</template>

<script>
import { defineComponent } from "vue";
import { ref } from "vue";

export default defineComponent({
  name: "ToDoContainer",
  components: {
  },

  props: {
    data: {
     type: Array,
      default: ref("[]"),
    },
  },

  setup() {

  }
});

</script>

<style lang="scss">
#vue-example {
  width: 100%;
  margin-top: 15px;
  margin-bottom: 15px;
}

#drag-scope {
  display: flex;
  justify-content: space-around;
  background-color: white;
}

.column {
  flex: 1 1 350px;
}

.todo-container {
  display: flex;
  padding: 10px;
  margin: 10px;
  flex-flow: column nowrap;
  min-height: 106px;
}

.todo-item {
  padding: 3px;
  margin: 5px;
  color: black;
  font-size: 15px;
  background-color: grey;
  border: 3px solid lightgoldenrodyellow;
}

.regular .todo-item {
  background-color: #8bc34a;
}

.priority .todo-item {
  background-color: #e57373;
}

.done .todo-item {
  background-color: lightgrey;
  color: gray;
  text-decoration: line-through;
}

.handle {
  cursor: move;
}

.remove {
  color: red;
  float: right;
  cursor: pointer;
}

.task {
  border: 1px dotted white;
  padding: 5px;
}

.title {
  text-align: center;
  font-weight: bold;
  padding: 10px;
  margin: 5px;
}

.input-model {
  padding: 0px 30px;
}
</style>
