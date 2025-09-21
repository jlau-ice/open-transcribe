import router from '@/router'
import { useUserStore } from '@/store/user'
import checkAccess from '@/access/checkAccess'
import { ACCESS_ENUM } from '@/access/accessEnum'
let firstFetchLoginUser = true
router.beforeEach(async (to, _, next) => {
  const userStore = useUserStore();
  if (firstFetchLoginUser) {
    await userStore.fetchLoginUser();
    firstFetchLoginUser = false;
  }
  const loginUser = userStore.loginUser;
  const needRoles = (to?.meta?.access as string) ?? ACCESS_ENUM.NO_LOGIN;
  if (needRoles === ACCESS_ENUM.NO_LOGIN) {
    return next();
  }
  if (!loginUser || !loginUser.id) {
    if (to.path !== '/login') {
      return next('/login');
    }
    return next();
  }
  if (!checkAccess(loginUser, needRoles)) {
    if (to.path !== '/noAccess') {
      return next('/noAccess');
    }
    return next();
  }
  next();
});
