export default {
    namespaced: true,
    state: {
      currentContest: null,
    },
    mutations: {
      setCurrentContest(state, contest) {
        state.currentContest = contest;
      },
      clearCurrentContest(state) {
        state.currentContest = null;
      }
    },
    getters: {
      currentContest: state => state.currentContest,
    }
  };
  