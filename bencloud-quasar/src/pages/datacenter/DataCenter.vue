<template>
  <q-page class="q-pa-md">
    <div class="page-layout">
      <!-- Task Queue Section -->
      <div class="task-section">
        <h3 class="category-header">Task Queue</h3>
        <q-card flat class="task-card">
          <q-card-actions vertical class="q-px-none">
            <q-btn
              color="primary"
              class="full-width"
              push
              @click="$router.replace('/datacenter/manage-tasks')"
              label="Pending and Completed Tasks"
            />
          </q-card-actions>
          <q-card-section class="q-px-none">
            <p class="description task-description">
              Access your analysis results and also see status of other long-running processes such as grid definition import, air quality import, and result export.
            </p>
          </q-card-section>
        </q-card>
      </div>

      <div class="vertical-divider"></div>

      <!-- Data Categories Section -->
      <div class="categories-section">
        <div class="categories-grid">
          <!-- Mapping and Air Quality -->
          <div class="category">
            <h3 class="category-header">Mapping and Air Quality</h3>
            <div class="category-cards">
              <q-card v-for="(card, index) in mappingCards" 
                     :key="index"
                     flat 
                     class="data-card">
                <q-card-actions vertical class="q-px-none">
                  <q-btn
                    color="primary"
                    class="full-width"
                    push
                    :disable="!card.route"
                    :class="{ 'disabled-btn': !card.route }"
                    @click="card.route && $router.replace(card.route)"
                    :label="card.label"
                  />
                </q-card-actions>
                <q-card-section class="q-px-none">
                  <p class="description" :class="{ 'clamp-text': !card.showFull }">
                    {{ card.description }}
                  </p>
                  <q-btn
                    v-if="card.hasOverflow"
                    flat
                    dense
                    color="primary"
                    :label="card.showFull ? 'Less' : 'More'"
                    class="more-link"
                    @click="toggleDescription(card)"
                  />
                </q-card-section>
              </q-card>
            </div>
          </div>

<!-- Health -->
<div class="category">
            <h3 class="category-header">Health</h3>
            <div class="category-cards">
              <q-card v-for="(card, index) in healthCards" 
                     :key="index"
                     flat 
                     class="data-card">
                <q-card-actions vertical class="q-px-none">
                  <q-btn
                    color="primary"
                    class="full-width"
                    push
                    :disable="!card.route"
                    :class="{ 'disabled-btn': !card.route }"
                    @click="card.route && $router.replace(card.route)"
                    :label="card.label"
                  />
                </q-card-actions>
                <q-card-section class="q-px-none">
                  <p class="description" :class="{ 'clamp-text': !card.showFull }">
                    {{ card.description }}
                  </p>
                  <q-btn
                    v-if="card.hasOverflow"
                    flat
                    dense
                    color="primary"
                    :label="card.showFull ? 'Less' : 'More'"
                    class="more-link"
                    @click="toggleDescription(card)"
                  />
                </q-card-section>
              </q-card>
            </div>
          </div>
          <!-- Population -->
          <div class="category">
            <h3 class="category-header">Population</h3>
            <div class="category-cards">
              <q-card v-for="(card, index) in populationCards" 
                     :key="index"
                     flat 
                     class="data-card">
                <q-card-actions vertical class="q-px-none">
                  <q-btn
                    color="primary"
                    class="full-width"
                    push
                    :disable="!card.route"
                    :class="{ 'disabled-btn': !card.route }"
                    @click="card.route && $router.replace(card.route)"
                    :label="card.label"
                  />
                </q-card-actions>
                <q-card-section class="q-px-none">
                  <p class="description" :class="{ 'clamp-text': !card.showFull }">
                    {{ card.description }}
                  </p>
                  <q-btn
                    v-if="card.hasOverflow"
                    flat
                    dense
                    color="primary"
                    :label="card.showFull ? 'Less' : 'More'"
                    class="more-link"
                    @click="toggleDescription(card)"
                  />
                </q-card-section>
              </q-card>
            </div>
          </div>

          <!-- Valuation -->
          <div class="category">
            <h3 class="category-header">Valuation</h3>
            <div class="category-cards">
              <q-card v-for="(card, index) in valuationCards" 
                     :key="index"
                     flat 
                     class="data-card">
                <q-card-actions vertical class="q-px-none">
                  <q-btn
                    color="primary"
                    class="full-width"
                    push
                    :disable="!card.route"
                    :class="{ 'disabled-btn': !card.route }"
                    @click="card.route && $router.replace(card.route)"
                    :label="card.label"
                  />
                </q-card-actions>
                <q-card-section class="q-px-none">
                  <p class="description" :class="{ 'clamp-text': !card.showFull }">
                    {{ card.description }}
                  </p>
                  <q-btn
                    v-if="card.hasOverflow"
                    flat
                    dense
                    color="primary"
                    :label="card.showFull ? 'Less' : 'More'"
                    class="more-link"
                    @click="toggleDescription(card)"
                  />
                </q-card-section>
              </q-card>
            </div>
          </div>

          
        </div>
      </div>
    </div>
  </q-page>
</template>

<script>
import { defineComponent, ref, reactive } from 'vue';

export default defineComponent({
  name: 'DataCenter',
  setup() {
    const showFullTaskDescription = ref(false);
    const isTaskDescriptionOverflow = ref(false);

    const mappingCards = reactive([
      {
        label: 'Grid Definitions',
        description: 'Shapefiles used to define the spatial domain over which various data inputs are available.',
        showFull: false,
        hasOverflow: false,
        route: '/datacenter/review-grids'
      },
      {
        label: 'Air Quality Surfaces and Data',
        description: 'Air pollution data (either model-simulated or monitor-measured) arranged spatially to align with an existing grid definition, where each grid cell contains an air quality value.',
        showFull: false,
        hasOverflow: true,
        route: '/datacenter/review-air-quality'
      }
    ]);

    const populationCards = reactive([
      {
        label: 'Population Counts',
        description: 'Census-based counts of the population in specific spatial units.',
        showFull: false,
        hasOverflow: false,
        route: null
      },
      {
        label: 'Population Characteristics',
        description: 'Social, demographic, and economic data.',
        showFull: false,
        hasOverflow: false,
        route: null
      }
    ]);

    const valuationCards = reactive([
      {
        label: 'Valuation Functions',
        description: 'Functions to calculate the economic value of changes in incidence of adverse health outcomes.',
        showFull: false,
        hasOverflow: false,
        route: null
      },
      {
        label: 'Inflation Datasets',
        description: 'All Goods Index, Medical Cost Index, and Wage Index used to inflate monetized health benefits.',
        showFull: false,
        hasOverflow: false,
        route: null
      }
    ]);

    const healthCards = reactive([
    {
        label: 'Health Incidence Data',
        description: 'Baseline, or background, rate of incidence or prevalence of a given adverse health effect. Incidence is the average number of adverse health effects (e.g., respiratory hospital admissions) per person per unit of time (e.g., daily or yearly). Prevalence is the percentage of individuals who experience or have been diagnosed with an adverse health effect at any given time (e.g., asthmatics).',
        showFull: false,
        hasOverflow: true,
        route: '/datacenter/review-incidence'
      },
      {
        label: 'Health Impact Functions',
        description: 'Concentration-response functions to calculate the change in the number of adverse health outcomes.',
        showFull: false,
        hasOverflow: false,
        route: null
      }
      
    ]);

    const toggleDescription = (card) => {
      card.showFull = !card.showFull;
    };

    return {
      showFullTaskDescription,
      isTaskDescriptionOverflow,
      mappingCards,
      populationCards,
      valuationCards,
      healthCards,
      toggleDescription
    };
  }
});
</script>

<style scoped>
.page-layout {
  display: flex;
  gap: 24px;
  height: 100%;
  flex-wrap: wrap; 
}

.task-section {
  flex: 1.2;
  max-width: 400px;
  border-right: 1.3px solid #999999;
  padding-right: 40px;
  padding-left: 20px;
  margin-right: 40px;
}

.categories-section {
  flex: 2.8;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px; 
}

.category-header {
  font-size: 22px;
  font-weight: semi-bold;
  margin-bottom: 16px;
  color: #333;
}

.category-cards {
  display: flex;
  flex-direction: column;
  gap: 0px;
}

.task-card,
.data-card {
  border: 1px solid transparent;
  box-shadow: none;
  position: relative;
  margin-bottom: 12px;
}

.q-card-section.q-px-none {
  padding-left: 0;
  padding-right: 0;
  position: relative;
  min-height: 50px;
}

.description {
  font-size: 14px;
  line-height: 1.4;
  color: #666;
  margin: 6px 0;
  max-width: calc(100% - 20px);
}

.task-description {
  max-width: 100%;
  white-space: normal;
  overflow: visible;
}

.clamp-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
}

.q-btn {
  height: 40px;
}

.q-card-section.q-px-none {
  padding-left: 0;
  padding-right: 0;
  position: relative; 
  min-height: 50px;
}

.more-link {
  padding: 0;
  margin: 0;
  min-height: unset;
  text-decoration: underline;
  position: absolute;
  bottom: -10px; 
}

.full-width {
  width: 100%;
  max-width: 260px;
}

.disabled-btn {
  cursor: not-allowed !important;
  opacity: 0.7;
}

.data-card {
 border: 1px solid transparent;
 box-shadow: none;
 position: relative;
 margin-bottom: 16px;
 max-width: 350px;
}
.q-card-actions.q-px-none {
  padding-left: 0;
  padding-right: 0;
}

@media (max-width: 900px) {
  .page-layout {
    flex-direction: column;
    gap: 16px;
  }

  .task-section {
    max-width: 100%;
    border-right: none;
    padding-right: 0;
    padding-left: 0;
    margin-right: 0;
  }

  .categories-grid {
    grid-template-columns: 1fr;
  }

  .data-card {
    max-width: 100%;
  }
}

@media (min-width: 1200px) {
  .page-layout {
    justify-content: left;
  }

  .task-section {
    max-width: 350px;
  }

  .categories-section {
    max-width: 900px;
  }
}
</style>