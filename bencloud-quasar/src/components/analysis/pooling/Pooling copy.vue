<template>
  <div id="vue-example">
    <div class="input-model">
      <input
        v-model="newTodoText"
        v-on:keyup.enter="addNewTodo"
        placeholder="Add a todo"
      />
    </div>
    <div id="drag-scope">
      <div class="column">
        <div class="title">Tasks</div>
        <ToDoContainer class="regular green lighten-5" :data="regular">
          <ToDoItem
            v-for="(items, index) in regular"
            :key="items.item"
            :data="items.item"
            @remove="regular.splice(index, 1)"
          ></ToDoItem>
        </ToDoContainer>
      </div>
      <div class="column">
        <div class="title">High Priority</div>
        <ToDoContainer class="priority red-4" :data="priority">
          <ToDoItem
            v-for="(items, index) in priority"
            :key="items.item"
            :data="items.item"
            @remove="priority.splice(index, 1)"
          ></ToDoItem>
        </ToDoContainer>
      </div>
      <div class="column">
        <div class="title">Done</div>
        <ToDoContainer class="done grey lighten-2" :data="done">
          <ToDoItem
            v-for="(items, index) in done"
            :key="items.item"
            :data="items.item"
            @remove="done.splice(index, 1)"
          ></ToDoItem>
        </ToDoContainer>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { vm, ref, toRef, toRefs, unref, isReactive, reactive, onMounted } from "vue";
import { useStore } from "vuex";
import { dom } from 'quasar'
import ToDoItem from "./ToDoItem.vue";
import ToDoContainer from "./ToDoContainer.vue";

export default defineComponent({
  model: ref(null),
  name: "Pooling",

  components: {
    ToDoItem,
    ToDoContainer,
  },

  setup(props, context) {
    const store = useStore();
    const el = ref("");

    const regular = reactive([
      { item: "Do the dishes" },
      { item: "Take out the trash" },
      { item: "Mow the lawn" },
    ]);

    const priority = reactive([
      { item: "Sweep the floor" },
      { item: "Take a shower" },
    ]);

    const done = reactive([
      { item: "Eat Breakfast" },
    ]);
/*    
    const todos = reactive({
      regular: [
        { item: "Do the dishes" },
        { item: "Take out the trash" },
        { item: "Mow the lawn" },
      ],
      priority: [],
      done: [],
    });
*/
    onMounted(() => {
      console.log(document.getElementById("drag-scope"));

      lmdd.set(document.getElementById("drag-scope"), {
        containerClass: "todo-container",
        draggableItemClass: "todo-item",
        handleClass: "handle",
        dataMode: true,
      });

      //"#vue-example".addEventListener("lmddend", this.handleDragEvent);
      var ve = document.querySelector("#vue-example");

      ve.addEventListener("lmddend", handleDragEvent);
      
    });

    return {
      newTodoText: "",
      addNewTodo,
      handleDragEvent,
      el,
      regular,
      priority,
      done,
    };

    function addNewTodo() {
      console.log(this.regular);
      this.regular.push({ item: this.newTodoText });
      console.log(this.regular);
      this.newTodoText = "";
    }

    function handleDragEvent(event) {
      console.log("handleDragEvent");
      console.log(event);
      var newIndex = event.detail.to.index;
      var oldIndex = event.detail.from.index;
      console.log("oldIndex: " + oldIndex)
      console.log("newIndex: " + newIndex)

      var newContainer = event.detail.to.container;
      var oldContainer = event.detail.from.container;

      var newContainerX = ref(event.detail.to.container);
      var oldContainerX = ref(event.detail.from.container);
      console.log(newContainerX.value)
      console.log(oldContainerX)
      var obj = reactive({ newContainerX })

      newContainer = event.detail.to.container.__vueParentComponent.props.data;
      oldContainer = event.detail.from.container.__vueParentComponent.props.data;
      console.log(oldContainer)
      console.log(newContainer)

      if (event.detail.dragType === "move") {
        newContainer.splice(newIndex, 0, oldContainer.splice(oldIndex, 1)[0]);
      }
      

/*
      var fromContainer = event.detail.to.container.__vueParentComponent.data;
      var toContainer = event.detail.from.container.__vueParentComponent.data;
      if (event.detail.dragType === "move") {
        toContainer.splice(newIndex, 0, fromContainer.splice(oldIndex, 1)[0]);
      }
*/

      //var newContainer = event.detail.to.container.__vue__.data;
      //var oldContainer = event.detail.from.container.__vue__.data;

      //console.log(dom.offset(oldContainer))
      //console.log(this.$refs.event.detail.to)
      /*
      var fromContainer = document.querySelector(event.detail.to.container);
      var toContainer = document.querySelector(event.detail.from.container);
      if (event.detail.dragType === "move") {
        toContainer.splice(newIndex, 0, fromContainer.splice(oldIndex, 1)[0]);
      }
      */
    }
  },
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
