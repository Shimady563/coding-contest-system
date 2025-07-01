import { createStore } from 'vuex';
import contest from './modules/contest';

const store = createStore({
  modules: {
    contest,
  }
});

export default store;
