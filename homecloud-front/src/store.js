import { createStore } from 'vuex';

const store = createStore({
  state: {
    userData: null
  },
  mutations: {
    SET_USER_INFO(state, userInfo) {
      state.userData = userInfo;
    },
    SET_USER_DATA(state, userData) {
      state.userData = userData;
      localStorage.setItem('userData', JSON.stringify(userData));
    },
    CLEAR_USER_DATA() {
      localStorage.removeItem('userData');
    }
  },
  actions: {
    setUserData({ commit }, userData) {
      commit('SET_USER_DATA', userData);
    },
    loadStoredUserData({ commit }) {
      const storedUserData = localStorage.getItem('userData');
      if (storedUserData) {
        commit('SET_USER_DATA', JSON.parse(storedUserData));
      }
    }
  }
});

export default store;

