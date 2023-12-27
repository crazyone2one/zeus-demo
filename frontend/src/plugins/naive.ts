import {
  // component
  NButton,
  NCard,
  NCheckbox,
  NCheckboxGroup,
  NDataTable,
  NDropdown,
  NForm,
  NFormItem,
  NFormItemRow,
  NIcon,
  NInput,
  NPopover,
  NPopselect,
  NResult,
  NSelect,
  NSkeleton,
  NSpace,
  NSpin,
  NSwitch,
  NTooltip,
  // create naive ui
  create,
} from 'naive-ui'

const naive = create({
  components: [
    NButton,
    NSelect,
    NInput,
    NSwitch,
    NForm,
    NFormItem,
    NTooltip,
    NDropdown,
    NIcon,
    NDataTable,
    NCheckbox,
    NSpace,
    NCheckboxGroup,
    NSkeleton,
    NSpin,
    NPopover,
    NResult,
    NFormItemRow,
    NCard,
    NPopselect,
  ],
})

export default naive
