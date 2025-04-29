#!/bin/bash

total_get_count=0
total_post_count=0
total_patch_count=0
total_put_count=0
total_delete_count=0

echo "API Count Summary"
echo "=========================="

for module_path in $(find ./ -type d -mindepth 1 -maxdepth 1); do
  module_name=$(basename "$module_path")
  controller_path="$module_path/src/main/java"

  if [ -d "$controller_path" ]; then
    controllers=$(find "$controller_path" -type f -name "*Controller*.java" 2>/dev/null)

    if [ -n "$controllers" ]; then
      module_has_api=false

      for controller_file in $controllers; do

        controller_name=$(basename "$controller_file")
        get_count=$(grep "@GetMapping" "$controller_file" | wc -l)
        post_count=$(grep "@PostMapping" "$controller_file" | wc -l)
        patch_count=$(grep "@PatchMapping" "$controller_file" | wc -l)
        put_count=$(grep "@PutMapping" "$controller_file" | wc -l)
        delete_count=$(grep "@DeleteMapping" "$controller_file" | wc -l)

        if (( get_count > 0 || post_count > 0 || patch_count > 0 || put_count > 0 || delete_count > 0 )); then
          module_has_api=true
          echo "  - $controller_name"
          ((get_count > 0)) && echo "    ├── GET APIs: $get_count"
          ((post_count > 0)) && echo "    ├── POST APIs: $post_count"
          ((patch_count > 0)) && echo "    ├── PATCH APIs: $patch_count"
          ((put_count > 0)) && echo "    ├── PUT APIs: $put_count"
          ((delete_count > 0)) && echo "    └── DELETE APIs: $delete_count"
        fi

        # 전체 합산
        total_get_count=$((total_get_count + get_count))
        total_post_count=$((total_post_count + post_count))
        total_patch_count=$((total_patch_count + patch_count))
        total_put_count=$((total_put_count + put_count))
        total_delete_count=$((total_delete_count + delete_count))
      done

      $module_has_api && echo "=========================="
    fi
  fi
done

total_api_count=$((total_get_count + total_post_count + total_patch_count + total_put_count + total_delete_count))

echo "API Statistics"
echo "=========================="
((total_get_count > 0)) && echo "- GET APIs: $total_get_count"
((total_post_count > 0)) && echo "- POST APIs: $total_post_count"
((total_patch_count > 0)) && echo "- PATCH APIs: $total_patch_count"
((total_put_count > 0)) && echo "- PUT APIs: $total_put_count"
((total_delete_count > 0)) && echo "- DELETE APIs: $total_delete_count"
((total_api_count > 0)) && echo "- Total APIs: $total_api_count"
echo "=========================="