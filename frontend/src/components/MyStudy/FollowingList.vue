<template>
  <div>
    <div class="d-flex flex-row align-items-center mt-3">
      <div class="pl-3" @click="goprofile(following)">
        <v-img class="thumbnail" v-if="following.userThumbnail != null" :src="'data:' + following.imageType + ';base64,' + following.userThumbnail"></v-img>
        <img v-show="!following.userThumbnail" class="thumbnail" src="../../../public/mystudy/userprofile/default.jpg">
      </div>
      <div class="mx-3 font_k" @click="goprofile(following)">{{ following.userName }}</div>
    </div>
    <hr>
  </div>
</template>

<script>
export default {
  name: 'FollowingList',
  props: {
    following: {
      type: Object
    }
  },
  methods: {
    goprofile(following) {
      this.$router.push({name: 'MyStudy', params: { UID:following.id }})
    },
  }
}
</script>

<style scoped>
.thumbnail {
  object-fit: cover;
  border-radius: 90%;
  width: 70px;
  /* max-width: 100%; */
  height: 70px;
}
</style>